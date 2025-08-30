import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Card,
  Descriptions,
  Tag,
  Button,
  Space,
  Steps,
  Timeline,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Image,
} from 'antd';
import { ArrowLeftOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { kycApi } from '../services/api';
import { KycProcess, WorkflowNode } from '../types';

const KycDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [data, setData] = useState<KycProcess | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      loadData();
    }
  }, [id]);

  const loadData = async () => {
    if (!id) return;
    
    try {
      setLoading(true);
      const response = await kycApi.getKycDetail(id);
      
      if (response.success) {
        setData(response.data);
      }
    } catch (error) {
      console.error('Failed to load KYC detail:', error);
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!id) return;
    
    try {
      const response = await kycApi.deleteKyc(id);
      if (response.success) {
        message.success('删除成功');
        navigate('/kyc');
      }
    } catch (error) {
      console.error('Failed to delete KYC:', error);
    }
  };

  const getStatusConfig = (status: string) => {
    const configs = {
      PENDING: { color: 'default', text: '等待中' },
      IN_PROGRESS: { color: 'processing', text: '处理中' },
      COMPLETED: { color: 'success', text: '已完成' },
      FAILED: { color: 'error', text: '失败' },
    };
    return configs[status as keyof typeof configs] || configs.PENDING;
  };

  const getNodeStatusConfig = (status: string) => {
    const configs = {
      PENDING: { color: 'default', text: '等待中' },
      IN_PROGRESS: { color: 'processing', text: '处理中' },
      COMPLETED: { color: 'success', text: '已完成' },
      FAILED: { color: 'error', text: '失败' },
    };
    return configs[status as keyof typeof configs] || configs.PENDING;
  };

  const getNodeTypeText = (type: string) => {
    const types = {
      ID_VERIFICATION: '身份证验证',
      FACE_VERIFICATION: '人脸验证',
      DEEPFAKE_DETECTION: '深度伪造检测',
    };
    return types[type as keyof typeof types] || type;
  };

  const getImageUrl = (filePath: string) => {
    if (!filePath) return undefined;
    const filename = filePath.substring(filePath.lastIndexOf('\\') + 1).substring(filePath.lastIndexOf('/') + 1);
    return `http://localhost:8080/api/admin/image/${filename}`;
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!data) {
    return (
      <Card>
        <p>未找到相关数据</p>
        <Button onClick={() => navigate('/kyc')}>返回列表</Button>
      </Card>
    );
  }

  const statusConfig = getStatusConfig(data.status);

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate('/kyc')}
          >
            返回列表
          </Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={loadData}
          >
            刷新
          </Button>
          <Popconfirm
            title="确认删除"
            description="删除后无法恢复，确定要删除这条记录吗？"
            onConfirm={handleDelete}
            okText="确认"
            cancelText="取消"
          >
            <Button
              type="primary"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      </div>

      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card title="基本信息">
            <Descriptions column={2}>
              <Descriptions.Item label="KYC ID">
                <code>{data.id}</code>
              </Descriptions.Item>
              <Descriptions.Item label="用户ID">
                <code>{data.userId}</code>
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={statusConfig.color}>{statusConfig.text}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="创建时间">
                {dayjs(data.createdTime).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
              <Descriptions.Item label="更新时间">
                {dayjs(data.updatedTime).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="身份证信息">
            {data.idCardInfo ? (
              <div>
                <Descriptions column={1}>
                  <Descriptions.Item label="文件名">
                    {data.idCardInfo.fileName}
                  </Descriptions.Item>
                  <Descriptions.Item label="上传时间">
                    {dayjs(data.idCardInfo.uploadTime).format('YYYY-MM-DD HH:mm:ss')}
                  </Descriptions.Item>
                  <Descriptions.Item label="验证状态">
                    <Tag color={getStatusConfig(data.idCardInfo.verificationStatus).color}>
                      {getStatusConfig(data.idCardInfo.verificationStatus).text}
                    </Tag>
                  </Descriptions.Item>
                </Descriptions>
                {getImageUrl(data.idCardInfo.filePath) && (
                  <div style={{ marginTop: 16 }}>
                    <strong>身份证图片：</strong>
                    <div style={{ marginTop: 8 }}>
                      <Image
                        width={200}
                        src={getImageUrl(data.idCardInfo.filePath)}
                        placeholder="加载中..."
                        fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3Ik1RUG8A8YCaG4lQqG4v1+CCgEFhQZQQyGgjKlYoYOQY6AGywCg4IDQ4ZMwhwYCWEwhgwZcpQOhgwMDhYZGhwSGRoYDgwNCgtBBJkGqhXNP3e3p1u1"
                      />
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <p>未上传身份证</p>
            )}
          </Card>
        </Col>

        <Col span={12}>
          <Card title="人脸照片信息">
            {data.faceInfo ? (
              <div>
                <Descriptions column={1}>
                  <Descriptions.Item label="文件名">
                    {data.faceInfo.fileName}
                  </Descriptions.Item>
                  <Descriptions.Item label="上传时间">
                    {dayjs(data.faceInfo.uploadTime).format('YYYY-MM-DD HH:mm:ss')}
                  </Descriptions.Item>
                  <Descriptions.Item label="验证状态">
                    <Tag color={getStatusConfig(data.faceInfo.verificationStatus).color}>
                      {getStatusConfig(data.faceInfo.verificationStatus).text}
                    </Tag>
                  </Descriptions.Item>
                </Descriptions>
                {getImageUrl(data.faceInfo.filePath) && (
                  <div style={{ marginTop: 16 }}>
                    <strong>人脸照片：</strong>
                    <div style={{ marginTop: 8 }}>
                      <Image
                        width={200}
                        src={getImageUrl(data.faceInfo.filePath)}
                        placeholder="加载中..."
                        fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3Ik1RUG8A8YCaG4lQqG4v1+CCgEFhQZQQyGgjKlYoYOQY6AGywCg4IDQ4ZMwhwYCWEwhgwZcpQOhgwMDhYZGhwSGRoYDgwNCgtBBJkGqhXNP3e3p1u1"
                      />
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <p>未上传人脸照片</p>
            )}
          </Card>
        </Col>

        {data.workflowNodes && data.workflowNodes.length > 0 && (
          <Col span={24}>
            <Card title="工作流进度">
              <Timeline>
                {data.workflowNodes.map((node: WorkflowNode) => {
                  const nodeStatus = getNodeStatusConfig(node.status);
                  return (
                    <Timeline.Item
                      key={node.nodeId}
                      color={nodeStatus.color}
                    >
                      <div>
                        <Space>
                          <strong>{getNodeTypeText(node.nodeType)}</strong>
                          <Tag color={nodeStatus.color}>{nodeStatus.text}</Tag>
                        </Space>
                        <div style={{ marginTop: 8, fontSize: 12, color: '#666' }}>
                          {node.startTime && (
                            <div>开始时间: {dayjs(node.startTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                          )}
                          {node.endTime && (
                            <div>结束时间: {dayjs(node.endTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                          )}
                          {node.taskId && (
                            <div>任务ID: <code>{node.taskId}</code></div>
                          )}
                        </div>
                      </div>
                    </Timeline.Item>
                  );
                })}
              </Timeline>
            </Card>
          </Col>
        )}

        {data.finalResult && (
          <Col span={24}>
            <Card title="最终结果">
              <Descriptions column={1}>
                <Descriptions.Item label="验证结果">
                  <Tag color={data.finalResult.passed ? 'success' : 'error'}>
                    {data.finalResult.passed ? '通过' : '未通过'}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="原因">
                  {data.finalResult.reason}
                </Descriptions.Item>
                <Descriptions.Item label="置信度">
                  {(data.finalResult.confidence * 100).toFixed(1)}%
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>
        )}
      </Row>
    </div>
  );
};

export default KycDetail;