import axios from 'axios';
import { message } from 'antd';
import { ApiResponse, KycProcess, PageResult, KycListParams } from '../types';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    message.error(error.response?.data?.message || '网络请求失败');
    return Promise.reject(error);
  }
);

export const kycApi = {
  // 获取KYC列表
  getKycList: (params: KycListParams): Promise<ApiResponse<PageResult<KycProcess>>> => {
    return api.get('/admin/kyc', { params }).then(res => res.data);
  },

  // 获取KYC详情
  getKycDetail: (kycId: string): Promise<ApiResponse<KycProcess>> => {
    return api.get(`/admin/kyc/${kycId}`).then(res => res.data);
  },

  // 删除KYC流程
  deleteKyc: (kycId: string): Promise<ApiResponse<string>> => {
    return api.delete(`/admin/kyc/${kycId}`).then(res => res.data);
  },
};

export default api;