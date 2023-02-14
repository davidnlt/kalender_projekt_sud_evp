<template>
  <v-app>
    <v-main>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card class="elevation-0">
              <div class="text-center">
                <h1>Account erstellen</h1>
              </div>
              <v-space></v-space>
              <v-card-text>
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

                  <v-btn type="submit" x-large block dark rounded
                    >Registrieren</v-btn
                  >
                </form>
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import axios from "axios";

export default {
  name: "UserRegistration",
  data() {
    return {
      vorname: "",
      nachname: "",
      abteilung: "",
      username: "",
      password: "",
      rules: [
        (value) => {
          if (value) return true;
          return "Füllen Sie dieses Feld aus.";
        },
      ],
    };
  },
  methods: {
    async register() {
      await axios
        .post("http://localhost:8080/UserRegistration", {
          vorname: this.vorname,
          nachname: this.nachname,
          abteilung: this.abteilung,
          benutzername: this.benutzername,
          passwort: this.passwort,
        })
        .then((response) => console.log(response))
        .catch((error) => console.log(error));
    },
  },
};
</script>
