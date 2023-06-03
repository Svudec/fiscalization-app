import { useEffect, useState } from 'react'
import { Spin, message } from 'antd'
import { invoiceUrl } from '../routes'
import { roundToTwoDecimalPlaces } from '../utils/generalUtils'
import { parseISO, format } from 'date-fns'
import axios from 'axios'
import './styles.css'

const r = (number) => roundToTwoDecimalPlaces(number)

const InvoiceItems = ({ items }) => (
  <>
    {items.map((item) => (
      <tr key={item.id}>
        <td>{item.productNumber}</td>
        <td>{item.name}</td>
        <td>{item.quantity}</td>
        <td>{item.unit}</td>
        <td>{r(item.grossPrice + item.grossPrice * (item.taxPercentage / 100))}</td>
      </tr>
    ))}
  </>
)

const TaxBreakdown = ({ categories }) => (
  <>
    {categories.map((category, index) => (
      <tr key={index}>
        <td>{r(category.osnovica)}</td>
        <td>{category.stopaPdv}%</td>
        <td>{r(category.iznos)}</td>
      </tr>
    ))}
  </>
)

export const InvoiceDetails = ({ invoiceId }) => {
  const [messageApi, contextHolder] = message.useMessage()
  const [invoice, setInvoice] = useState(undefined)

  useEffect(() => {
    if (invoiceId) {
      axios
        .get(invoiceUrl(invoiceId))
        .then((res) => setInvoice(res.data))
        .catch((err) => messageApi.open({ type: 'error', content: err.message }))
    }
  }, [])

  return (
    <>
      {contextHolder}
      {invoice ? (
        <div className="invoice-details">
          <h1>Račun broj: {invoice.invoiceNumber}</h1>
          <div className="invoice-details-grid">
            <p>Datum izdavanja: {format(parseISO(invoice.invoiceDate), 'dd.MM.yyyy. HH:mm')}</p>
            <p>Način plaćanja: {invoice.paymentType}</p>
            <p>Operator: {invoice.createdBy}</p>
            <p>Operator ID: {invoice.operatorId}</p>
            {invoice.invoiceFiscalizationStatus === 'FISKALIZIRANO' && (
              <>
                <p>ZKI: {invoice.zki}</p>
                <p>JIR: {invoice.jir}</p>
              </>
            )}
          </div>
          <br />
          {invoice.note && (
            <>
              <p>Napomena: {invoice.note}</p>
              <br />
            </>
          )}
          <p>
            <strong>Ukupan iznos: {r(invoice.inTotal)}</strong>
          </p>
          {invoice.invoiceFiscalizationStatus === 'NEUSPJESNO_FISKALIZIRANO' && (
            <p>Razlog pogreške: {invoice.fiscalizationMessage}</p>
          )}
          <br />
          <table className="invoice-details-table">
            <caption>Proizvodi:</caption>
            <thead>
              <tr>
                <th>Oznaka</th>
                <th>Proizvod</th>
                <th>Količina</th>
                <th>Obračunska jedinica</th>
                <th>Cijena (NETO)</th>
              </tr>
            </thead>
            <tbody>
              <InvoiceItems items={invoice.invoiceItems} />
            </tbody>
          </table>
          <br />
          <table className="invoice-details-tax-table">
            <caption>Obračunati porez:</caption>
            <thead>
              <tr>
                <th>Osnovica</th>
                <th>Stopa</th>
                <th>Iznos</th>
              </tr>
            </thead>
            <tbody>
              <TaxBreakdown categories={invoice.taxCategories} />
            </tbody>
          </table>
        </div>
      ) : (
        <Spin />
      )}
    </>
  )
}
