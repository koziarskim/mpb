<template>
    <money :value.sync="myvalue" v-bind="money"></money>
</template>
<script>
import { Money } from 'v-money'

export default {
  extends: Money,
  props: {
    value: {
      required: true,
      type: [Number, String],
      default: 0
    },
  },
  components: {Money},
  data () {
    return {
      money: {
        decimal: '.',
        thousands: ',',
        prefix: '',
        suffix: '',
        precision: 0,
        masked: false
      },
    }
  },
  computed:{
      myvalue: {
        get: function () {
          return this.value;
        },
        set: function (newValue) {
          this.$emit('value', newValue)
        }
    }
  },
  watch:{
  },
  mounted () {
  }
}
</script>

<style>
</style>