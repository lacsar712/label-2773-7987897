import { request } from '../utils/request';
import type { ConfigGroupVO, ConfigGroupUpdateDTO, SystemConfigDTO, PageResult, ConfigHistoryVO } from '../types/systemConfig';

export const systemConfigApi = {
  getAllGroups: () => {
    return request.get<ConfigGroupVO[]>('/api/system/config/groups');
  },

  getGroup: (groupCode: string) => {
    return request.get<ConfigGroupVO>(`/api/system/config/group/${groupCode}`);
  },

  updateConfig: (dto: SystemConfigDTO) => {
    return request.post<boolean>('/api/system/config/update', dto);
  },

  updateGroup: (dto: ConfigGroupUpdateDTO) => {
    return request.post<boolean>('/api/system/config/group/update', dto);
  },

  queryHistory: (configGroup?: string, configKey?: string, pageNum = 1, pageSize = 10) => {
    const params = new URLSearchParams();
    if (configGroup) params.append('configGroup', configGroup);
    if (configKey) params.append('configKey', configKey);
    params.append('pageNum', String(pageNum));
    params.append('pageSize', String(pageSize));
    return request.get<PageResult<ConfigHistoryVO>>(`/api/system/config/history?${params.toString()}`);
  },

  exportGroup: (groupCode: string) => {
    return request.get<Blob>(`/api/system/config/export/${groupCode}`, {
      responseType: 'blob'
    });
  },

  importGroup: (file: File, updatedBy = 'system') => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('updatedBy', updatedBy);
    return request.post<boolean>('/api/system/config/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },

  getConfigValue: (group: string, key: string) => {
    return request.get<string>(`/api/system/config/value?group=${group}&key=${key}`);
  }
};
