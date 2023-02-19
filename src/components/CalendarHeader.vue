<template>
  <v-app-bar color="white">
    <v-toolbar-title>
      Willkommen {{ vorname }} im "Abteilung-Urblaubskalender"
    </v-toolbar-title>

    <v-spacer></v-spacer>
    <v-toolbar-item>
      <v-dialog v-model="dialog" persistent width="1024">
        <template v-slot:activator="{ props }">
          <v-btn @click="dialog = true" v-bind="props"
            ><v-icon>mdi-account-edit</v-icon> Dein Konto
          </v-btn>
        </template>
        <v-card>
          <v-card-title>
            <span class="text-h5">Benutzerprofil bearbeiten</span>
          </v-card-title>
          <v-card-text>
            <v-container>
              <form ref="form" @submit.prevent="register()">
                <v-text-field
                  v-model="vorname"
                  name="vornme"
                  label="Vorname"
                  type="text"
                  color="black"
                  :rules="rules"
                  outlined
                  rounded
                ></v-text-field>

                <v-text-field
                  v-model="nachname"
                  name="nachname"
                  label="Nachname"
                  type="text"
                  color="black"
                  :rules="rules"
                  outlined
                  rounded
                ></v-text-field>

                <v-text-field
                  v-model="abteilung"
                  name="abteilung"
                  label="Abteilung"
                  type="text"
                  color="black"
                  :rules="rules"
                  outlined
                  rounded
                ></v-text-field>

                <v-text-field
                  v-model="username"
                  name="username"
                  label="Benutzername"
                  type="text"
                  color="black"
                  :rules="rules"
                  outlined
                  rounded
                ></v-text-field>

                <v-text-field
                  v-model="password"
                  name="password"
                  label="Passwort"
                  type="password"
                  color="black"
                  :rules="rules"
                  outlined
                  rounded
                ></v-text-field>
                <v-btn
                  color="blue-darken-1"
                  variant="text"
                  @click="dialog = false"
                >
                  Schließen
                </v-btn>
                <v-btn
                  type="submit"
                  color="blue-darken-1"
                  variant="text"
                  @click="dialog = false"
                >
                  Änderungen speichern
                </v-btn>
              </form>
            </v-container>
          </v-card-text>
        </v-card>
      </v-dialog>

      <v-btn @click="logout()">
        <v-icon>mdi-logout</v-icon>
        Abmelden
      </v-btn>
    </v-toolbar-item>
  </v-app-bar>
</template>

<script>
export default {
  name: "CalendarHeader",
  data() {
    return {
      vorname: "David",
      nachname: "Nolte",
      abteilung: "",
      username: "",
      password: "",
      dialog: false,
    };
  },
  methods: {
    logout() {
      this.$router.push({ name: "UserLogin" });
    },
  },
};
</script>
