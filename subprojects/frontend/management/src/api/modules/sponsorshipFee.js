const getList = (client, params) => client.request({
  url: '/v1/sponsorshipFees',
  method: 'get',
  params,
  local: true,
});

export default {
  getList,
};
