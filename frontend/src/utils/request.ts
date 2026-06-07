import axios from 'axios';
import { message } from 'ant-design-vue';

const service = axios.create({
  baseURL: '/api', // Base URL for all requests
  timeout: 5000,   // Request timeout
});

// Request interceptor
service.interceptors.request.use(
  (config) => {
    // You can add auth token here if needed
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers['Authorization'] = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    
    // Check custom status code (assuming 200 is success)
    if (res.code !== 200) {
      message.error(res.message || 'Error');
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res;
    }
  },
  (error) => {
    console.error('Response error:', error);
    const msg = error.response?.data?.message || error.message || 'Network Error';
    message.error(msg);
    return Promise.reject(error);
  }
);

export default service;
