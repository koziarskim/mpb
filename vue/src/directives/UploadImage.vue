<template>
  <div style="border: solid 1px #c1c4c7; width: 150px; height:150px;" @click="openFileSelect">
    <a href="#" v-b-popover.hover="'Click to select new image'">
      <img :src="imageUrl" height="100%">
    </a>
  </div>
</template>
<script>
export default {
  inheritAttributes: true,
  props: {
    fileUrl: {
      validator: prop =>
        typeof prop === "string" || prop === null || prop === undefined,
      required: true
    },
    onUpload: {
      type: Function,
      required: true
    }
  },
  data() {
    return {
	  compressedImage: null,
	  inputElement: null,
    };
  },
  computed: {
    imageUrl() {
      if (this.compressedImage) {
        return this.compressedImage;
	  }
	  if(this.fileUrl){
		return this.fileUrl;
	  }
	  return require("@/assets/image-select.png");
    }
  },
  methods: {
	openFileSelect() {
      this.inputElement = document.createElement("input");
      this.inputElement.type = "file";
      this.inputElement.onchange = this.uploadImage;
	  this.inputElement.accept = "image/png, image/jpeg";
      this.inputElement.click();
    },  
    uploadImage() {
      var file = this.inputElement.files[0];
      if (file) {
        var reader = new FileReader();
        reader.onload = imgUploadEvent => {
          var img = new Image();
          img.onload = () => {
            var oc = document.createElement("canvas");
            var octx = oc.getContext("2d");
            var maxWidth = 150;
            var percentage =
              img.width > maxWidth
                ? maxWidth / img.width
                : img.width / maxWidth;
            oc.width = img.width * percentage;
            oc.height = img.height * percentage;
            octx.drawImage(img, 0, 0, oc.width, oc.height);
            this.compressedImage = oc.toDataURL();
            this.onUpload(this.dataURItoBlob(this.compressedImage));
          };
          img.src = imgUploadEvent.target.result;
        };
        reader.readAsDataURL(this.inputElement.files[0]);
      }
    },
    dataURItoBlob(dataURI) {
      if (!dataURI) {
        return null;
      }
      var byteString;
      if (dataURI.split(",")[0].indexOf("base64") >= 0)
        byteString = atob(dataURI.split(",")[1]);
      else byteString = unescape(dataURI.split(",")[1]);
      var mimeString = dataURI
        .split(",")[0]
        .split(":")[1]
        .split(";")[0];
      var ia = new Uint8Array(byteString.length);
      for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
      }
      return new Blob([ia], { type: mimeString });
    }
  }
};
</script>