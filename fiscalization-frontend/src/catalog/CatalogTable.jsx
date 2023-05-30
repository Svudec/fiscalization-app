import { Button, Table, message } from 'antd'
import { EditOutlined, DeleteOutlined } from '@ant-design/icons'
import Column from 'antd/es/table/Column'
import ColumnGroup from 'antd/es/table/ColumnGroup'
import { roundToTwoDecimalPlaces } from '../utils/generalUtils'
import './styles.css'
import { useEffect, useState } from 'react'
import axios from 'axios'
import { catalogAllUrl } from '../routes'

const r = (number) => roundToTwoDecimalPlaces(number)

export const CatalogTable = () => {
  const [catalogItems, setCatalogItems] = useState([])
  const [messageApi, contextHolder] = message.useMessage()

  useEffect(() => {
    axios
      .get(catalogAllUrl)
      .then((res) => console.log(res))
      .catch((err) => messageApi.open({ type: 'error', content: err.message }))
  }, [])

  //   const catalogItemsExample = [
  //     {
  //       id: 1,
  //       name: 'Example Item',
  //       description: 'This is an example item',
  //       productNumber: 'ABC1',
  //       grossPrice: 100000.0,
  //       taxPercentage: 10.0,
  //       unit: 'kom'
  //     },
  //     {
  //       id: 2,
  //       name: 'Example Item',
  //       productNumber: 'ABC1',
  //       grossPrice: 100.1,
  //       taxPercentage: 20.0,
  //       unit: 'kom'
  //     }
  //   ].map((i) => ({
  //     ...i,
  //     grossPrice: r(i.grossPrice),
  //     netPrice: r(i.grossPrice + i.grossPrice * (i.taxPercentage / 100))
  //   }))

  return (
    <>
      {contextHolder}
      <Table
        bordered
        caption="Katalog proizvoda"
        dataSource={catalogItems}
        rowKey="id"
        pagination={{ hideOnSinglePage: true }}
        expandable={{
          expandedRowRender: (record) => <p style={{ margin: 0 }}>{record.description}</p>,
          rowExpandable: (record) => !!record.description
        }}>
        <Column title="Identifikator" dataIndex="productNumber" />
        <Column title="Naziv" dataIndex="name" />
        <Column title="Jedinica" dataIndex="unit" />
        <ColumnGroup title="Cijena" align="center" key="price">
          <Column title="BRUTO" dataIndex="grossPrice" align="right" />
          <Column title="PDV(%)" dataIndex="taxPercentage" align="right" />
          <Column title="NETO" dataIndex="netPrice" align="right" />
        </ColumnGroup>
        <Column
          title="Akcije"
          key="actions"
          align="center"
          render={() => (
            <div className="catalog-table-action-buttons-container">
              <Button type="primary" icon={<EditOutlined />} />
              <Button type="primary" icon={<DeleteOutlined />} danger />
            </div>
          )}
        />
      </Table>
    </>
  )
}
