<template>
  <div class="home__index">
    <h1>欢迎来到IOT平台管理系统</h1>
  </div>
</template>

<script>
// @ is an alias to /src

export default {
  name: "Welcome",
  components: {}
};
</script>
<style lang="less">
.home__index {
  height: 100%;
}
h1 {
  text-align: center;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -100%);
  margin-left: -125px;
}
</style>
