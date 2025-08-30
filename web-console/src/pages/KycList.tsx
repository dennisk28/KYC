import React, { useEffect, useState } from 'react';
import {
  Table,
  Card,
  Button,
  Select,
  Space,
  Tag,
  Popconfirm,
  message,
  Input,
} from 'antd';
import { DeleteOutlined, EyeOutlined, ReloadOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { kycApi } from '../services/api';
import { KycProcess } from '../types';

const { Search } = Input;

const KycList: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<KycProcess[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0,
  });
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const columns = [
    {
      title: 'KYC ID',
      dataIndex: 'id',
      key: 'id',
      width: 120,
      render: (text: string) => (
        <span style={{ fontFamily: 'monospace', fontSize: 12 }}>
          {text.substring(0, 8)}...
        </span>
      ),
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      key: 'userId',
      width: 120,
      render: (text: string) => (
        <span style={{ fontFamily: 'monospace', fontSize: 12 }}>
          {text.substring(0, 8)}...
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const statusConfig = {
          PENDING: { color: 'default', text: '等待中' },
          IN_PROGRESS: { color: 'processing', text: '处理中' },
          COMPLETED: { color: 'success', text: '已完成' },
          FAILED: { color: 'error', text: '失败' },
        };
        const config = statusConfig[status as keyof typeof statusConfig] || statusConfig.PENDING;
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 150,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedTime',
      key: 'updatedTime',
      width: 150,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '身份证',
      key: 'idCard',
      width: 80,
      render: (record: KycProcess) => {
        return record.idCardInfo ? 
          <Tag color="green">已上传</Tag> : 
          <Tag color="default">未上传</Tag>;
      },
    },
    {
      title: '人脸照片',
      key: 'facePhoto',
      width: 80,
      render: (record: KycProcess) => {
        return record.faceInfo ? 
          <Tag color="green">已上传</Tag> : 
          <Tag color="default">未上传</Tag>;
      },
    },
    {
      title: '最终结果',
      key: 'finalResult',
      width: 100,
      render: (record: KycProcess) => {
        if (!record.finalResult) {
          return <Tag color="default">待定</Tag>;
        }
        return record.finalResult.passed ? 
          <Tag color="success">通过</Tag> : 
          <Tag color="error">未通过</Tag>;
      },
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      render: (record: KycProcess) => (
        <Space>
          <Button
            type="primary"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => navigate(`/kyc/${record.id}`)}
          >
            详情
          </Button>
          <Popconfirm
            title="确认删除"
            description="删除后无法恢复，确定要删除这条记录吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="primary" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize, statusFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      const response = await kycApi.getKycList({
        page: pagination.current - 1,
        size: pagination.pageSize,
        status: statusFilter,
      });

      if (response.success && response.data) {
        setData(response.data.content || []);
        setPagination(prev => ({
          ...prev,
          total: response.data.totalElements || 0,
        }));
      }
    } catch (error) {
      console.error('Failed to load KYC data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      const response = await kycApi.deleteKyc(id);
      if (response.success) {
        message.success('删除成功');
        loadData();
      }
    } catch (error) {
      console.error('Failed to delete KYC:', error);
    }
  };

  const handleTableChange = (newPagination: any) => {
    setPagination({
      ...pagination,
      current: newPagination.current,
      pageSize: newPagination.pageSize,
    });
  };

  return (
    <div>
      <Card>
        <div style={{ marginBottom: 16 }}>
          <Space>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 120 }}
            >
              <Select.Option value="ALL">全部状态</Select.Option>
              <Select.Option value="PENDING">等待中</Select.Option>
              <Select.Option value="IN_PROGRESS">处理中</Select.Option>
              <Select.Option value="COMPLETED">已完成</Select.Option>
              <Select.Option value="FAILED">失败</Select.Option>
            </Select>
            
            <Button icon={<ReloadOutlined />} onClick={loadData}>
              刷新
            </Button>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`,
          }}
          onChange={handleTableChange}
          scroll={{ x: 1200 }}
        />
      </Card>
    </div>
  );
};

export default KycList;