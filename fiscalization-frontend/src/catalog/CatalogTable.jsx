import { Button, Popconfirm, Table, message } from 'antd'
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import Column from 'antd/es/table/Column'
import ColumnGroup from 'antd/es/table/ColumnGroup'
import { roundToTwoDecimalPlaces } from '../utils/generalUtils'
import './styles.css'
import { useEffect, useState } from 'react'
import axios from 'axios'
import { catalogAllUrl, catalogUrl } from '../routes'
import { CatalogFormModal } from './CatalogFormModal'

const r = (number) => roundToTwoDecimalPlaces(number)

export const CatalogTable = () => {
  const [catalogItems, setCatalogItems] = useState([])
  const [messageApi, contextHolder] = message.useMessage()
  const [isFormModalOpened, setIsFormModalOpened] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)

  useEffect(() => {
    if(selectedItem === null){
        axios
        .get(catalogAllUrl)
        .then((res) =>
          setCatalogItems(
            res.data.map((i) => ({
              ...i,
              grossPrice: r(i.grossPrice),
              netPrice: r(i.grossPrice + i.grossPrice * (i.taxPercentage / 100))
            }))
          )
        )
        .catch((err) => messageApi.open({ type: 'error', content: err.message }))
        .finally(() => setSelectedItem(undefined))
    }
  }, [selectedItem])

  const handleFormSubmitted = (updatedItem) => {
    setIsFormModalOpened(false)
    setSelectedItem(null)
  }

  const handleDeleteItem = (id) => {
    axios
      .delete(catalogUrl(id))
      .then(() => {
        setCatalogItems((prev) => prev.filter((item) => item.id !== id))
        messageApi.open({ type: 'success', content: 'Brisanje uspješno' })
      })
      .catch((err) => messageApi.open({ type: 'error', content: err.message }))
  }

  return (
    <>
      {contextHolder}
      <div className="catalog-table-title-wrapper">
        <div className="catalog-table-title">Katalog proizvoda</div>
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
        dataSource={catalogItems}
        rowKey="id"
        pagination={{ hideOnSinglePage: true }}
        expandable={{
          expandedRowRender: (record) => <p style={{ margin: 0 }}>{record.description}</p>,
          rowExpandable: (record) => !!record.description
        }}
        locale={{ emptyText: 'Nema proizvoda' }}>
        <Column title="Identifikator" dataIndex="productNumber" />
        <Column title="Naziv" dataIndex="name" />
        <Column title="Jedinica" dataIndex="unit" />
        <ColumnGroup title="Cijena" align="center" key="price">
          <Column title="BRUTO(€)" dataIndex="grossPrice" align="right" />
          <Column title="PDV(%)" dataIndex="taxPercentage" align="right" />
          <Column title="NETO(€)" dataIndex="netPrice" align="right" />
        </ColumnGroup>
        <Column
          title="Akcije"
          key="actions"
          align="center"
          width="20px"
          render={(value) => (
            <div className="catalog-table-action-buttons-container">
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
                description="Želite li trajno izbrisati proizvod?"
                okText="DA"
                cancelText="NE"
                placement='left'
                onConfirm={() => {
                  handleDeleteItem(value.id)
                }}>
                <Button type="primary" icon={<DeleteOutlined />} danger />
              </Popconfirm>
            </div>
          )}
        />
      </Table>
      <CatalogFormModal
        isOpened={isFormModalOpened}
        catalogItemId={selectedItem}
        onCancel={() => setIsFormModalOpened(false)}
        onOk={handleFormSubmitted}
      />
    </>
  )
}
