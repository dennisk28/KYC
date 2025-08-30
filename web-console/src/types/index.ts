export interface KycProcess {
  id: string;
  userId: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  createdTime: string;
  updatedTime: string;
  idCardInfo?: IdCardInfo;
  faceInfo?: FaceInfo;
  workflowNodes?: WorkflowNode[];
  finalResult?: FinalResult;
}

export interface IdCardInfo {
  fileName: string;
  filePath: string;
  uploadTime: string;
  verificationStatus: 'PENDING' | 'SUCCESS' | 'FAILED';
  verificationResult?: any;
}

export interface FaceInfo {
  fileName: string;
  filePath: string;
  uploadTime: string;
  verificationStatus: 'PENDING' | 'SUCCESS' | 'FAILED';
  verificationResult?: any;
}

export interface WorkflowNode {
  nodeId: string;
  nodeName: string;
  nodeType: 'ID_VERIFICATION' | 'FACE_VERIFICATION' | 'DEEPFAKE_DETECTION';
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  startTime?: string;
  endTime?: string;
  result?: any;
  taskId?: string;
}

export interface FinalResult {
  passed: boolean;
  reason: string;
  confidence: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errorCode?: string;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface KycListParams {
  page: number;
  size: number;
  status: string;
}