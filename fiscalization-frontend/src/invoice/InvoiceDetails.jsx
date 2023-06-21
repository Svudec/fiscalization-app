import { useEffect, useState } from 'react'
import { Spin, message } from 'antd'
import { invoiceUrl } from '../routes'
import { roundToTwoDecimalPlaces } from '../utils/generalUtils'
import { parseISO, format } from 'date-fns'
import axios from 'axios'
import './styles.css'

const r = (number) => roundToTwoDecimalPlaces(number)

export const nacinPlacanjaToString = (ulaz) => {
  switch (ulaz) {
    case 'KARTICA':
      return 'Bankovna kartica'
    case 'CEK':
      return 'Ček'
    case 'TRANSAKCIJSKI_RACUN':
      return 'Transakcijski račun'
    case 'GOTOVINA':
      return 'Gotovina'
    default:
      return 'Ostalo'
  }
}

const InvoiceItems = ({ items }) => (
  <>
    {items.map((item) => (
      <tr key={item.id}>
        <td>{item.productNumber}</td>
        <td>{item.name}</td>
        <td className="right">{item.quantity}</td>
        <td className="right">{item.unit}</td>
        <td className="right">
          {r(item.grossPrice + item.grossPrice * (item.taxPercentage / 100))}
        </td>
        <td className="right">
          {r((item.grossPrice + item.grossPrice * (item.taxPercentage / 100)) * item.quantity)}
        </td>
      </tr>
    ))}
  </>
)

const TaxBreakdown = ({ categories }) => (
  <>
    {categories.map((category, index) => (
      <tr key={index}>
        <td className="right">{r(category.osnovica)}</td>
        <td className="right">{category.stopaPdv}%</td>
        <td className="right">{r(category.iznos)}</td>
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
        .catch((err) =>
          messageApi.open({
            type: 'error',
            content: err.response?.data?.errorMessage ?? err.message
          })
        )
    }
  }, [])

  return (
    <>
      {contextHolder}
      {invoice ? (
        <div className="invoice-details">
          <div>
            <p>FIRA Solutions d.o.o.</p>
            <p>Trg Josipa Jurja Strossmayera 8, 10000 Zagreb</p>
            <p>OIB: 21233832319</p>
          </div>
          <h1>Račun broj: {invoice.invoiceNumber}</h1>
          <div className="invoice-details-grid">
            <p>
              Datum izdavanja: {format(parseISO(invoice.invoiceDate + 'Z'), 'dd.MM.yyyy. HH:mm')}
            </p>
            <p>Način plaćanja: {nacinPlacanjaToString(invoice.paymentType)}</p>
            <p>Operator: {invoice.createdBy}</p>
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
            <p style={{ color: '#bb0000' }}>Razlog pogreške: {invoice.fiscalizationMessage}</p>
          )}
          <br />
          <table className="invoice-details-table">
            <caption>Proizvodi:</caption>
            <thead>
              <tr>
                <th>Oznaka</th>
                <th>Proizvod</th>
                <th className="right">Količina</th>
                <th className="right">Obračunska jedinica</th>
                <th className="right">Cijena po jedinici (NETO)</th>
                <th className="right">Ukupna cijena</th>
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
                <th className="right">Osnovica</th>
                <th className="right">Stopa</th>
                <th className="right">Iznos</th>
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
