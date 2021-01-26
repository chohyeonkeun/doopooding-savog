const join = (client, data) => client.request({
  url: '/v1/users/join',
  method: 'post',
  data,
  local: true,
});

const login = (client, data) => client.request({
  url: '/v1/users/login',
  method: 'post',
  data,
  local: true,
});

export default {
  join,
  login,
};
