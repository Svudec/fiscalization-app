import { Form, Input, InputNumber, Modal, Radio, Select, message, Button } from 'antd'
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { catalogAllUrl, invoiceBaseUrl, invoiceUrl } from '../routes'
import axios from 'axios'
import './styles.css'

export const InvoiceFormModal = ({ invoiceId, isOpen, onCancel, onOk }) => {
  const [messageApi, contextHolder] = message.useMessage()
  const [form] = Form.useForm()
  const invoiceItems = Form.useWatch('invoiceItems', form)
  const [catalogItems, setCatalogItems] = useState([])
  const [unitLabels, setUnitLabels] = useState([])
  const unusedCatalogItems = useMemo(
    () => catalogItems?.filter((i) => !invoiceItems?.some((inv) => inv?.catalogItemId === i.id)),
    [catalogItems, invoiceItems]
  )
  const myCatalogItems = useCallback(
    (name) => {
      const myItem = catalogItems.find((ci) => ci.id === invoiceItems?.at(name)?.catalogItemId)
      return myItem ? [myItem, ...unusedCatalogItems] : unusedCatalogItems
    },
    [catalogItems, invoiceItems]
  )

  useEffect(() => {
    setUnitLabels(
      invoiceItems?.map((i) => catalogItems.find((c) => c.id === i?.catalogItemId)?.unit)
    )
  }, [invoiceItems])

  useEffect(() => {
    axios
      .get(catalogAllUrl)
      .then((res) =>
        setCatalogItems(
          res.data.map((i) => ({ ...i, label: i.productNumber + ' ' + i.name, value: i.id }))
        )
      )
      .catch((err) =>
        messageApi.open({ type: 'error', content: err.response?.data?.errorMessage ?? err.message })
      )
  }, [])

  useEffect(() => {
    if (catalogItems) {
      if (invoiceId) {
        axios
          .get(invoiceUrl(invoiceId))
          .then((res) => form.setFieldsValue(res.data))
          .catch((err) =>
            messageApi.open({
              type: 'error',
              content: err.response?.data?.errorMessage ?? err.message
            })
          )
      } else {
        form.setFieldsValue({
          invoiceNumber: undefined,
          paymentType: undefined,
          createdBy: undefined,
          note: undefined,
          invoiceItems: []
        })
      }
    }
  }, [invoiceId, catalogItems])

  const createRequest = (body) => {
    axios
      .post(invoiceBaseUrl, { ...body, invoiceDate: new Date() })
      .then((res) => {
        messageApi.open({
          type: 'success',
          content: 'Uspješno kreiran račun broj ' + res?.data?.invoiceNumber
        })
        onOk && onOk()
      })
      .catch((err) => {
        messageApi.open({ type: 'error', content: err.response?.data?.errorMessage ?? err.message })
      })
  }

  const updateRequest = (body) => {
    axios
      .put(invoiceBaseUrl, { ...body, invoiceDate: new Date(), id: invoiceId })
      .then((res) => {
        messageApi.open({
          type: 'success',
          content: 'Uspješno ažuriran račun broj ' + res?.data?.invoiceNumber
        })
        onOk && onOk()
      })
      .catch((err) => {
        messageApi.open({
          type: 'error',
          content: err.response?.data?.errorMessage ?? err.message
        })
      })
  }

  const submitForm = () => {
    form
      .validateFields()
      .then((values) => {
        invoiceId ? updateRequest(values) : createRequest(values)
      })
      .catch((errors) => {
        console.log(errors)
      })
  }

  return (
    <>
      {contextHolder}
      <Modal
        title={
          form.getFieldValue('invoiceNumber')
            ? 'Račun broj ' + form.getFieldValue('invoiceNumber')
            : 'Novi račun'
        }
        closable={false}
        open={isOpen}
        onCancel={onCancel}
        onOk={submitForm}
        width={'860px'}
        okText="Spremi"
        cancelText="Odustani"
        destroyOnClose>
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          validateMessages={{ required: 'Podatak je obavezan!' }}
          form={form}>
          <Form.Item name={'id'} hidden>
            <Input />
          </Form.Item>
          <Form.Item name={'invoiceDate'} hidden>
            <Input />
          </Form.Item>
          <Form.Item name={'invoiceNumber'} label="Broj" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name={'paymentType'} label="Način plaćanja" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value={'KARTICA'}>Kartica</Radio>
              <Radio value={'GOTOVINA'}>Gotovina</Radio>
              <Radio value={'CEK'}>Ček</Radio>
              <Radio value={'TRANSAKCIJSKI_RACUN'}>Transakcijski račun</Radio>
              <Radio value={'OSTALO'}>Ostalo</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item name={'createdBy'} label="Račun izdao" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name={'note'} label="Napomena">
            <Input.TextArea />
          </Form.Item>
          <Form.List
            name={'invoiceItems'}
            rules={[
              {
                validator: async (_, items) => {
                  if (!items || items.length === 0) {
                    return Promise.reject(new Error('Račun mora imati barem jedan proizvod!'))
                  }
                }
              }
            ]}>
            {(fields, { add, remove }, { errors }) => (
              <>
                {fields.map(({ key, name, ...restField }) => (
                  <div key={key} className="invoice-form-modal-product-row">
                    <Form.Item
                      {...restField}
                      name={[name, 'catalogItemId']}
                      rules={[{ required: true }]}>
                      <Select
                        placeholder="Odaberi proizvod"
                        showSearch
                        filterOption={(input, option) =>
                          (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                        options={myCatalogItems(name)}
                      />
                    </Form.Item>
                    <Form.Item
                      {...restField}
                      name={[name, 'quantity']}
                      rules={[{ required: true }]}>
                      <InputNumber
                        placeholder="Količina"
                        addonAfter={unitLabels?.at(name)}
                        disabled={!unitLabels?.at(name)}
                        precision={2}
                      />
                    </Form.Item>
                    <MinusCircleOutlined onClick={() => remove(name)} />
                  </div>
                ))}
                <div className="invoice-form-modal-product-row-add-button">
                  <Form.Item>
                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                      Dodaj proizvod
                    </Button>
                    <Form.ErrorList errors={errors} />
                  </Form.Item>
                </div>
              </>
            )}
          </Form.List>
        </Form>
      </Modal>
    </>
  )
}
