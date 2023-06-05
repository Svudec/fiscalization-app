import './App.css'
import { Layout, Menu } from 'antd'
import { CatalogTable } from './catalog/CatalogTable'
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import { InvoiceTable } from './invoice/InvoiceTable'
import { BarcodeOutlined, FileTextOutlined } from '@ant-design/icons'

const { Header, Content, Footer } = Layout

const menuItems = [
  { label: 'Katalog', key: 'catalog', icon: <BarcodeOutlined /> },
  { label: 'Računi', key: 'invoices', icon: <FileTextOutlined /> }
]

function App() {
  const location = useLocation()
  const navigate = useNavigate()
  return (
    <Layout className="rootStyles">
      <Header className="header">
        <div className="app-header">FISKALIZACIJA RAČUNA</div>
        <Menu
          style={{ width: '230px' }}
          selectedKeys={[location.pathname.slice(1)]}
          mode="horizontal"
          items={menuItems}
          onClick={(item) => {
            navigate('/' + item.key)
          }}
        />
      </Header>
      <Layout>
        {/* <Sider theme="light">left sidebar</Sider> */}
        <Content style={{ padding: '20px 40px' }}>
          <Routes>
            <Route path="/" element={<Navigate to={'/invoices'} replace />} />
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
