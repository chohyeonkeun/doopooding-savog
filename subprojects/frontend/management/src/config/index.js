const defaultConfig = {
  axios: {
    baseURL: __DEV__ ? 'http://localhost:8080' : '/api',
    timeout: 10 * 1000,
    withCredentials: true,
  },
};

export default defaultConfig;
