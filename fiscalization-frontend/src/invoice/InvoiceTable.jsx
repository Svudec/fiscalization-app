import { Button, Popconfirm, Table, message } from 'antd'
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
  AuditOutlined,
  CheckCircleTwoTone,
  QuestionCircleTwoTone,
  ClockCircleTwoTone,
  ExclamationCircleTwoTone,
  ShopTwoTone
} from '@ant-design/icons'
import Column from 'antd/es/table/Column'
import './styles.css'
import '../catalog/styles.css'
import { useEffect, useState } from 'react'
import axios from 'axios'
import { invoiceAllUrl, invoiceStartFiscalizationUrl, invoiceUrl } from '../routes'
import { roundToTwoDecimalPlaces } from '../utils/generalUtils'
import { parseISO, format } from 'date-fns'
import { InvoiceDetails, nacinPlacanjaToString } from './InvoiceDetails'
import { InvoiceFormModal } from './InvoiceFormModal'

const r = (number) => roundToTwoDecimalPlaces(number)

const renderStatus = (value) => {
  switch (value) {
    case 'NIJE_ZAPOCETO':
      return <ShopTwoTone style={{ fontSize: '24px' }} title="NIJE ZAPOCETO" />
    case 'U_OBRADI':
      return <ClockCircleTwoTone style={{ fontSize: '24px' }} title="U OBRADI" />
    case 'NEUSPJESNO_FISKALIZIRANO':
      return (
        <ExclamationCircleTwoTone
          twoToneColor="red"
          style={{ fontSize: '24px' }}
          title="GRESKA U FISKALIZACIJI"
        />
      )
    case 'FISKALIZIRANO':
      return (
        <CheckCircleTwoTone
          twoToneColor="#45ab13"
          style={{ fontSize: '24px' }}
          title="FISKALIZIRANO"
        />
      )
    default:
      return <QuestionCircleTwoTone title="NEPOZNATO" />
  }
}

export const InvoiceTable = () => {
  const [invoices, setInvoices] = useState([])
  const [messageApi, contextHolder] = message.useMessage()
  const [isFormModalOpened, setIsFormModalOpened] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)
  const [isFetching, setIsFetching] = useState(false)

  useEffect(() => {
    if (selectedItem === null) {
      setIsFetching(true)
      axios
        .get(invoiceAllUrl)
        .then((res) =>
          setInvoices(
            res.data.map((i) => ({
              ...i,
              inTotal: r(i.inTotal),
              invoiceDate: format(parseISO(i.invoiceDate + 'Z'), 'dd.MM.yyyy. HH:mm'),
              paymentType: nacinPlacanjaToString(i.paymentType)
            }))
          )
        )
        .catch((err) =>
          messageApi.open({
            type: 'error',
            content: err.response?.data?.errorMessage ?? err.message
          })
        )
        .finally(() => {
          setSelectedItem(undefined)
          setIsFetching(false)
        })
    }
  }, [selectedItem])

  const handleFormSubmitted = () => {
    setIsFormModalOpened(false)
    setSelectedItem(null)
  }

  const handleDeleteItem = (id) => {
    axios
      .delete(invoiceUrl(id))
      .then(() => {
        setInvoices((prev) => prev.filter((item) => item.id !== id))
        messageApi.open({ type: 'success', content: 'Brisanje uspješno' })
      })
      .catch((err) =>
        messageApi.open({ type: 'error', content: err.response?.data?.errorMessage ?? err.message })
      )
  }

  const startFiscalization = (id) => {
    axios
      .put(invoiceStartFiscalizationUrl(id))
      .then((res) => {
        const updatedIndex = invoices.findIndex((i) => i.id === id)
        setInvoices((prev) => [
          ...prev.slice(0, updatedIndex),
          {
            ...res.data,
            inTotal: r(res.data.inTotal),
            invoiceDate: format(parseISO(res.data.invoiceDate + 'Z'), 'dd.MM.yyyy. HH:mm'),
            paymentType: nacinPlacanjaToString(res.data.paymentType)
          },
          ...prev.slice(updatedIndex + 1)
        ])
        messageApi.open({ type: 'success', content: 'Fiskalizacija je započeta' })
      })
      .catch((err) =>
        messageApi.open({ type: 'error', content: err.response?.data?.errorMessage ?? err.message })
      )
  }

  return (
    <>
      {contextHolder}
      <div className="catalog-table-title-wrapper">
        <div className="catalog-table-title">Računi</div>
        <Button
          size="large"
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setSelectedItem(undefined)
            setIsFormModalOpened(true)
          }}
        />
      </div>
      <Table
        bordered
        dataSource={invoices}
        rowKey="id"
        pagination={{ hideOnSinglePage: true, pageSize: 8 }}
        expandable={{
          expandedRowRender: (record, indent, expanded) =>
            expanded && !isFetching ? <InvoiceDetails invoiceId={record.id} /> : null
        }}
        locale={{ emptyText: 'Nema računa' }}>
        <Column title="Broj računa" dataIndex="invoiceNumber" />
        <Column title="Vrijeme" dataIndex="invoiceDate" />
        <Column title="Kreirao" dataIndex="createdBy" />
        <Column title="Način plaćanja" dataIndex="paymentType" />
        <Column title="Ukupni iznos" dataIndex="inTotal" align="right" />
        <Column
          title="Status fiskalizacije"
          dataIndex="invoiceFiscalizationStatus"
          render={renderStatus}
          align="center"
        />
        <Column
          title="Akcije"
          key="actions"
          align="center"
          width="20px"
          render={(value) => (
            <div className="catalog-table-action-buttons-container">
              {['NIJE_ZAPOCETO', 'NEUSPJESNO_FISKALIZIRANO'].includes(
                value.invoiceFiscalizationStatus
              ) && (
                <>
                  <Button
                    type="primary"
                    icon={<AuditOutlined />}
                    onClick={() => {
                      startFiscalization(value.id)
                    }}>
                    Fiskaliziraj
                  </Button>

                  <Button
                    type="primary"
                    icon={<EditOutlined />}
                    onClick={() => {
                      setSelectedItem(value.id)
                      setIsFormModalOpened(true)
                    }}
                  />
                  <Popconfirm
                    title="Jeste li sigurni?"
                    description="Želite li trajno izbrisati račun?"
                    okText="DA"
                    cancelText="NE"
                    placement="left"
                    onConfirm={() => {
                      handleDeleteItem(value.id)
                    }}>
                    <Button type="primary" icon={<DeleteOutlined />} danger />
                  </Popconfirm>
                </>
              )}
            </div>
          )}
        />
      </Table>
      <InvoiceFormModal
        invoiceId={selectedItem}
        isOpen={isFormModalOpened}
        onCancel={() => setIsFormModalOpened(false)}
        onOk={handleFormSubmitted}
      />
    </>
  )
}
