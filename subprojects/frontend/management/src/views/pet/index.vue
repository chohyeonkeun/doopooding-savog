<template>
  <div>
    애완동물 리스트
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
      const PetApi = this.getApi(API.PET);
      this.data = PetApi.getList();
      console.log('petData - ', this.data);
    },
  },
};
</script>
