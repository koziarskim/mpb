<template>
    <b-container fluid>
      <vue-cal ref="vuecal" hide-view-selector :min-event-width=0 :events="events" :time-from="6 * 60" :time-to="18 * 60" :hide-weekdays="[7]">
        <template v-slot:event="{ event, view }">
          <div :id="'popover-'+event.id" @click="openPopover('popover-'+event.id)" style="font-size: 9px">{{event.title}}
          <div>
            {{event.content}}
          </div>
          </div>
            <b-popover :show="showPopover == 'popover-'+event.id" placement="top" :target="'popover-'+event.id">
              <template v-slot:title>Popover Title</template>
              I am popover <b>component</b> content!
            </b-popover>
        </template>
      </vue-cal>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      showPopover: false,
      events: [
        {
          id: 1,
          start: '2020-03-12 9:30',
          end: '2020-03-12 10:00',
          title: 'Walmart',
          defaultContent: 'Walmart (city & state)',
          extraContent: "Whatever",
          contet: "",
          class: 'health'
        },
        {
          id: 2,
          start: '2020-03-12 12:00',
          end: '2020-03-12 13:00',
          title: 'Big Lots',
          content: '<i class="v-icon material-icons">thumb_up</i>',
          class: 'blue-event'
        },
        {
          id: 3,
          start: '2020-03-12 12:00',
          end: '2020-03-12 13:00',
          title: 'Big Lots',
          content: '<i class="v-icon material-icons">thumb_up</i>',
          class: 'blue-event'
        },
        {
          id: 4,
          start: '2020-03-12 12:00',
          end: '2020-03-12 13:00',
          title: 'Big Lots',
          content: '<i class="v-icon material-icons">thumb_up</i>',
          class: 'blue-event'
        },
        {
          id: 5,
          start: '2020-12-13 11:30',
          end: '2018-11-20 14:30',
          title: 'Marketplace Brands',
          content: '<i class="v-icon material-icons">fitness_center</i>',
          class: 'sport'
        }
      ],
    };
  },
  methods: {
    openPopover(elName) {
      if(this.showPopover == elName){
        this.showPopover = "";
      }else{
        this.showPopover = elName;
      }
    }
  },
  mounted() {
  }
};
</script>

<style>
.vuecal__title-bar {
  min-height: 0 !important;
}
</style>
