const getList = (client, params) => client.request({
  url: '/v1/pets',
  method: 'get',
  params,
  local: true,
});

export default {
  getList,
};
