import './App.css'
import { Layout } from 'antd'

const { Header, Content, Footer, Sider } = Layout

function App() {
  return (
    <Layout className="rootStyles">
      <Header style={{ background: 'lightblue' }}>header</Header>
      <Layout>
        <Sider theme="light">left sidebar</Sider>
        <Content>main content</Content>
        <Sider theme="light">right sidebar</Sider>
      </Layout>
      <Footer>footer</Footer>
    </Layout>
  )
}

export default App
