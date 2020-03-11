<template>
    <b-container fluid>
      <vue-cal hide-view-selector :events="events"></vue-cal>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      events: [
        {
          start: '2020-03-12 8:00',
          end: '2020-03-12 11:00',
          title: 'LUNCH',
          class: 'lunch',
          background: true
        },
        {
          start: '2020-03-12 9:00',
          end: '2020-03-12 14:00',
          title: 'LUNCH2',
          class: 'lunch2',
          background: true
        }
      ],
    };
  },
  methods: {
  },
  mounted() {
  }
};
</script>

<style>
</style>
