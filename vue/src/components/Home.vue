<template>
    <b-container fluid>
        <b-row>
            <b-col cols=2>
                <div style="width: 200px; height: 490px; background-color: #3062ae; text-align: right; color: white;"><span style="padding-right: 15px">Annaucements</span></div>
            </b-col>
            <b-col cols=10>
                <b-row style="width: 1500px">
                    <b-col cols=2>
                        <div class="home-box-green"><span style="padding-right: 15px">Manage Suppliers</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-orange"><span style="padding-right: 15px">Add New Component</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-green"><span style="padding-right: 15px">Add New Item</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-orange"><span style="padding-right: 15px">Announcements</span></div>
                    </b-col>        
                </b-row>
                <b-row style="padding-top: 10px; width: 1500px">
                    <b-col cols=2>
                        <div class="home-box-green"><span style="padding-right: 15px">Manage Customers</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-blue"><span style="padding-right: 15px">Create Sales Order</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-navy"><span style="padding-right: 15px">Create Purchase Order</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-transparent"><span style="padding-right: 15px"></span></div>
                    </b-col>        
                </b-row>
            </b-col>
        </b-row>
    </b-container>
</template>
<style lang="scss">
.home-box-green {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #549e58;
}
.home-box-blue {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #3062ae;
}
.home-box-navy {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #106f87;
}
.home-box-orange {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #f0a40b;
}
.home-box-orange {
  color: white;
  text-align: right;
  text-align: right;
  width: 240px;
  height: 240px;
}
</style>
<script>
export default {
  name: "home"
};
</script>
