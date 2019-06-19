<template>
  <div class="v-suggestions">
    <input type="text" class="search-input"
           v-bind="$attrs"
           v-on:keydown="onKeyDown"
           v-on:blur="hideItems"
           v-on:focus="showItems = true"
           v-model="query"
           :autocomplete="Math.random()">
    <div class="suggestions">
      <ul class="items" v-show="items.length > 0 && showItems === true">
        <li class="item"
            :key="index"
            v-for="(item, index) in items"
            @click.prevent="selectItem(index)"
            v-bind:class="{ 'is-active': index === activeItemIndex }">
          <slot name="item"
                :item="item">
            {{item.label}}
          </slot>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
  import debounce from 'debounce'
  import '../css/Search.css'
  export default {
    inheritAttributes: true,
    props: {
      onEnter: {
          type: Function,
          default: function(){}
      },
      onInputChange: {
        type: Function,
        required: true
      },
      onItemSelected: {
        type: Function
      },
      selected: {
          type: [Object, String]
      },
      value: {
        type: [String, Object],
        required: true
      }
    },
    data () {
      return {
        query: this.value,
        lastSetQuery: null,
        items: [],
        activeItemIndex: -1,
        showItems: false
      }
    },
    beforeMount () {
        console.log("beforMount value: "+this.value)
        console.log("beforeMount selected: "+this.selected)
    },
    mounted(){
        console.log("Mount value: "+this.value)
        console.log("Mount selected: "+this.selected)
    },
    watch: {
      'query': function (newValue, oldValue) {
        console.log(newValue)
        if (newValue === this.lastSetQuery) {
          this.lastSetQuery = null
          return
        }
        this.onQueryChanged(newValue)
        this.$emit('input', newValue)
      },
      'value': function (newValue, oldValue) {
        console.log(newValue)
        this.setInputQuery(newValue)
      }
    },
    methods: {
      onEnterDefault(){
          this.hideItems();
          this.onEnter();
      },
      onItemSelectedDefault (item) {
        if (typeof item === 'object') {
          this.$emit('input', item.label)
          this.setInputQuery(item.label)
          this.showItems = false
        }
        this.onItemSelected(item);
      },
      hideItems () {
        setTimeout(() => {
          this.showItems = false
        }, 300)
      },
      showResults () {
        this.showItems = true
      },
      setInputQuery (value) {
        this.lastSetQuery = value
        this.query = value
      },
      onKeyDown (e) {
        this.$emit('keyDown', e.keyCode)
        switch (e.keyCode) {
          case 40:
            this.highlightItem('down')
            e.preventDefault()
            break
          case 38:
            this.highlightItem('up')
            e.preventDefault()
            break
          case 13:
            this.selectItem()
            e.preventDefault()
            break
          case 27:
            this.showItems = false
            e.preventDefault()
            break
          default:
            return true
        }
      },
      selectItem (index) {
        let item = null
        if (typeof index === 'undefined') {
          if (this.activeItemIndex === -1) {
              this.onEnterDefault();
            return
          }
          item = this.items[this.activeItemIndex]
        } else {
          item = this.items[index]
        }
        if (!item) {
          return
        }
        this.onItemSelectedDefault(item)
        this.hideItems()
      },
      highlightItem (direction) {
        if (this.items.length === 0) {
          return
        }
        let selectedIndex = this.items.findIndex((item, index) => {
          return index === this.activeItemIndex
        })
        if (selectedIndex === -1) {
          // nothing selected
          if (direction === 'down') {
            selectedIndex = 0
          } else {
            selectedIndex = this.items.length - 1
          }
        } else {
          this.activeIndexItem = 0
          if (direction === 'down') {
            selectedIndex++
            if (selectedIndex === this.items.length) {
              selectedIndex = 0
            }
          } else {
            selectedIndex--
            if (selectedIndex < 0) {
              selectedIndex = this.items.length - 1
            }
          }
        }
        this.activeItemIndex = selectedIndex
      },
      setItems (items) {
        this.items = items
        this.activeItemIndex = -1
        this.showItems = true
      },
      onQueryChanged (value) {
        console.log(this.items)
        const result = this.onInputChange(value)
        this.items = []
        if (typeof result === 'undefined' || typeof result === 'boolean' || result === null) {
          return
        }
        if (result instanceof Array) {
          this.setItems(result)
        } else if (typeof result.then === 'function') {
          result.then(items => {
            this.setItems(items)
          })
        }
      }
    }
  }
</script>