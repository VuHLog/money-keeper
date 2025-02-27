<script setup>
import Navbar from "@layouts/Navbar/Navbar.vue";
window.addEventListener("scroll", function () {
  var header = document.getElementById("header");
  if (window.scrollY > header.offsetHeight) {
    header.classList.add("sticky");
  } else {
    header.classList.remove("sticky");
  }
});
</script>

<template>
  <header id="header" class="bg-white position-sticky top-0 z-30 border-b-sm border-solid">
    <Navbar class="px-16"> </Navbar>
  </header>
</template>

<style lang="scss" scoped>
#header{
  &.sticky {
    box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
}
}
</style>