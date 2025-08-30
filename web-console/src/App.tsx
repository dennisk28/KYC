import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import KycList from './pages/KycList';
import KycDetail from './pages/KycDetail';

import './App.css';

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <div className="App">
          <Layout>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/kyc" element={<KycList />} />
              <Route path="/kyc/:id" element={<KycDetail />} />
            </Routes>
          </Layout>
        </div>
      </Router>
    </ConfigProvider>
  );
};

export default App;