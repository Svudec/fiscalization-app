import { Form, Input, InputNumber, Modal, message } from 'antd'
import { useCallback, useEffect } from 'react'
import { catalogBaseUrl, catalogUrl } from '../routes'
import axios from 'axios'

export const CatalogFormModal = ({ catalogItemId, isOpened, onCancel, onOk }) => {
  const [messageApi, contextHolder] = message.useMessage()
  const [form] = Form.useForm()

  useEffect(() => {
    if (catalogItemId) {
      axios
        .get(catalogUrl(catalogItemId))
        .then((res) => form.setFieldsValue(res.data))
        .catch((err) => messageApi.open({ type: 'error', content: err.message }))
    } else {
      form.setFieldsValue({
        id: undefined,
        name: undefined,
        description: undefined,
        productNumber: undefined,
        grossPrice: undefined,
        taxPercentage: 25.0,
        unit: 'kom'
      })
    }
  }, [catalogItemId])

  const createRequest = (body) => {
    axios
      .post(catalogBaseUrl, body)
      .then((res) => {
        messageApi.open({ type: 'success', content: 'Uspješno kreiran novi proizvod' })
        onOk && onOk(res.data)
      })
      .catch((err) => {
        messageApi.open({ type: 'error', content: err.message })
      })
  }

  const updateRequest = (body) => {
    axios
      .put(catalogBaseUrl, body)
      .then((res) => {
        messageApi.open({ type: 'success', content: 'Uspješno ažuriran proizvod' })
        onOk && onOk(res.data)
      })
      .catch((err) => {
        messageApi.open({ type: 'error', content: err.message })
      })
  }

  const submitForm = () => {
    form
      .validateFields()
      .then((values) => {
        values.id ? updateRequest(values) : createRequest(values)
      })
      .catch((errors) => {
        console.log(errors)
      })
  }

  return (
    <>
      {contextHolder}
      <Modal
        title={form.getFieldValue('name') ?? 'Novi proizvod'}
        closable={false}
        open={isOpened}
        onCancel={onCancel}
        onOk={submitForm}
        width={'800px'}
        okText="Spremi"
        cancelText="Odustani">
        <Form
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 19 }}
          validateMessages={{ required: 'Podatak je obavezan!' }}
          form={form}>
          <Form.Item name={'id'} hidden>
            <Input />
          </Form.Item>
          <Form.Item name={'name'} label="Naziv" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name={'productNumber'} label="Identifikator" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name={'grossPrice'} label="Cijena(BRUTO)" rules={[{ required: true }]}>
            <InputNumber addonAfter="€" precision={2} />
          </Form.Item>
          <Form.Item name={'taxPercentage'} label="Stopa PDV-a" rules={[{ required: true }]}>
            <InputNumber addonAfter="%" precision={0} />
          </Form.Item>
          <Form.Item name={'unit'} label="Obračunska jedinica" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name={'description'} label="Opis">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
