import './App.css'
import { Layout } from 'antd'
import { CatalogTable } from './catalog/CatalogTable'
import { Link, Route, Routes } from 'react-router-dom'
import { InvoiceTable } from './invoice/InvoiceTable'

const { Header, Content, Footer } = Layout

function App() {
  return (
    <Layout className="rootStyles">
      <Header className="app-header">FISKALIZACIJA RAČUNA</Header>
      <Layout>
        {/* <Sider theme="light">left sidebar</Sider> */}
        <Content style={{ padding: '20px 40px' }}>
          <Routes>
            <Route
              path="/"
              element={
                <div>
                  <Link to={'/catalog'}>Katalog</Link>
                  <br />
                  <Link to={'/invoices'}>Računi</Link>
                </div>
              }
            />
            <Route path="/catalog" element={<CatalogTable />} />
            <Route path="/invoices" element={<InvoiceTable />} />
          </Routes>
        </Content>
        {/* <Sider theme="light">right sidebar</Sider> */}
      </Layout>
      <Footer>
        <div style={{ textAlign: 'center' }}>
          Aplikacija za upravljanje fiskalnim računima u mikroservisnoj arhitekturi izrađena
          korištenjem platforme Kubernetes
        </div>
        <div style={{ textAlign: 'center' }}>Karlo Sudec, Mentor: prof. dr. sc. Boris Vrdoljak</div>
        <div style={{ textAlign: 'center' }}>Fakultet elektrotehnike i računarstva</div>
        <div style={{ textAlign: 'center' }}>Sveučilište u Zagrebu</div>
      </Footer>
    </Layout>
  )
}

export default App
