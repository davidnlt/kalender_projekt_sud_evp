<template>
  <v-footer color="white" padless>
    <v-row justify="center" no-gutters>
      <v-btn v-for="link in links" :key="link" color="black" text rounded>
        {{ link }}
      </v-btn>
    </v-row>
    <v-col class="text-center" cols="12">
      @ {{ new Date().getFullYear() }} <strong>Urlaubskalender</strong>
    </v-col>
  </v-footer>
</template>

<script>
export default {
  name: "CalendarFooter",
  data: () => ({
    links: ["Datenschutz", "Nutzungsbedingungen", "Impressum"],
  }),
};
</script>
