import { isEmpty, get, mapValues, has, assign } from 'lodash';
import axios from 'axios';
import config from 'config';
import user from './modules/user';
import pet from './modules/pet';
import sponsorshipFee from './modules/sponsorshipFee';
import proxy from 'api/proxy';

function initClient(vm) {
  const client = axios.create(config.axios);

  // request interceptor
  if (isEmpty(client.interceptors.request.handlers)) {
    client.interceptors.request.use((config) => {
      const userId = localStorage.getItem('loginUserId');
      const username = localStorage.getItem('loginUsername');
      config = assign(config, {
        headers: {
          'X-Requester-Id': userId,
          'X-Requester-Username': encodeURIComponent(username),
        },
      });
      return config;
    }, (error) => {
      return Promise.reject(error);
    });
  }

  // response interceptor
  if (isEmpty(client.interceptors.response.handlers)) {
    client.interceptors.response.use((response) => {
      if (has(response.data, 'success')) {
        return get(response.data, 'data', '');
      } else {
        return response.data;
      }
    }, (error) => {
      const status = get(error, 'response.status');
      const { code, message } = get(error, 'response.data.errors[0]', { code: '', message: '' });
      switch (status) {
        case 401: {
          const options = {
            color: 'danger',
            title: '세션시간이 만료되었습니다.',
            text: '로그인 페이지로 이동합니다.',
            vm,
          };
          // TODO: 에러 메시지 alert
          vm.$nextTick(() => {
            // TODO: 로그인 페이지 이동
          });
        }
          break;
        case 403: {
          const options = {
            title: '요청이 실패하였습니다. (Forbidden)',
            text: `권한이 존재하지 않습니다.(${message})`,
            vm,
          };
          // TODO: 에러 메시지 alert
        }
          break;
        // TODO: 여러 status 에 대한 대응 추가
        default: {
          const options = {
            title: `Unknown response. (status: ${status})`,
            text: 'Unknown response.',
            vm,
          };
          // TODO: 에러 메시지 alert
        }
          break;
      }
      return Promise.reject(error);
    });

    return client;
  }
}

const modules = {
  USER: user,
  PET: pet,
  SPONSORSHIPFEE: sponsorshipFee,
};

// { AUTH: "AUTH", ... } 의 값을 가지는 Map.
export const API = Object.keys(modules)
  .reduce((result, key) => {
    result[key] = key;
    return result;
  }, {});

export default {
  created() {
    const client = initClient(this);
    const source = axios.CancelToken.source();
    const apis = mapValues(modules, (module) => proxy(module, client));
    this.getApi = (name) => apis[name];
    this.cancelApi = source.cancel;
  },
  beforeDestroy() {
    this.cancelApi();
  },
};
