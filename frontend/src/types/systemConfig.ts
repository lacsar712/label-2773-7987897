export interface ConfigItemVO {
  id: number;
  configGroup: string;
  configKey: string;
  configValue: string;
  valueType: string;
  displayName: string;
  description: string;
  sortOrder: number;
  updatedBy: string;
  updatedAt: string;
}

export interface ConfigGroupVO {
  configGroup: string;
  displayName: string;
  description: string;
  configs: ConfigItemVO[];
}

export interface ConfigHistoryVO {
  id: number;
  configGroup: string;
  configKey: string;
  displayName: string;
  oldValue: string;
  newValue: string;
  changedBy: string;
  changedAt: string;
}

export interface SystemConfigDTO {
  configGroup: string;
  configKey: string;
  configValue: string;
  valueType: string;
  displayName: string;
  description: string;
  sortOrder: number;
  updatedBy: string;
}

export interface ConfigGroupUpdateDTO {
  configGroup: string;
  configs: SystemConfigDTO[];
  updatedBy: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
