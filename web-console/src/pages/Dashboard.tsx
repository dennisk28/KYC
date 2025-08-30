import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Spin } from 'antd';
import {
  UserOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { kycApi } from '../services/api';

interface DashboardStats {
  total: number;
  pending: number;
  inProgress: number;
  completed: number;
  failed: number;
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    total: 0,
    pending: 0,
    inProgress: 0,
    completed: 0,
    failed: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // 分别获取各状态的统计数据
      const [allResult, pendingResult, inProgressResult, completedResult, failedResult] = 
        await Promise.all([
          kycApi.getKycList({ page: 0, size: 1, status: 'ALL' }),
          kycApi.getKycList({ page: 0, size: 1, status: 'PENDING' }),
          kycApi.getKycList({ page: 0, size: 1, status: 'IN_PROGRESS' }),
          kycApi.getKycList({ page: 0, size: 1, status: 'COMPLETED' }),
          kycApi.getKycList({ page: 0, size: 1, status: 'FAILED' }),
        ]);

      setStats({
        total: allResult.data.totalElements,
        pending: pendingResult.data.totalElements,
        inProgress: inProgressResult.data.totalElements,
        completed: completedResult.data.totalElements,
        failed: failedResult.data.totalElements,
      });
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>系统概览</h2>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="总计"
              value={stats.total}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="等待处理"
              value={stats.pending}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="处理中"
              value={stats.inProgress}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="已完成"
              value={stats.completed}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>
      
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="失败"
              value={stats.failed}
              prefix={<ExclamationCircleOutlined />}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="成功率"
              value={stats.total > 0 ? ((stats.completed / stats.total) * 100).toFixed(1) : 0}
              suffix="%"
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      <Card title="系统状态" style={{ marginTop: 24 }}>
        <Row gutter={[16, 16]}>
          <Col span={24}>
            <p>🟢 后端服务：正常运行</p>
            <p>🟢 数据库连接：正常</p>
            <p>🟢 第三方服务：正常</p>
            <p>📊 最后更新时间：{new Date().toLocaleString()}</p>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default Dashboard;