<template>
  <div>
    후원금 리스트
  </div>
</template>

<script>
import ApiClient, { API } from 'api/client';

export default {
  extends: ApiClient,
  data() {
    return {
      data: null,
    };
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.loadData();
    },
    async loadData() {
      const SponsorshipFeeApi = this.getApi(API.SPONSORSHIP);
      this.data = SponsorshipFeeApi.getList();
      console.log('petData - ', this.data);
    },
  },
};
</script>
