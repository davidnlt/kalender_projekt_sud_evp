<template>
  <v-app>
    <v-main>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card class="elevation-0">
              <div class="text-center">
                <h1>Willkommen zurück</h1>
              </div>
              <v-card-text>
                <form ref="form" @submit.prevent="login()">
                  <v-text-field
                    v-model="username"
                    name="username"
                    label="Benutzername"
                    type="text"
                    color="black"
                    prepend-inner-icon="mdi-account"
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
                    prepend-inner-icon="mdi-lock"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-btn type="submit" x-large block dark rounded
                    >Anmelden</v-btn
                  >

                  <v-col class="text-right">
                    <v-btn to="/registration" x-large rounded>
                      Registrieren
                    </v-btn>
                  </v-col>
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
  name: "UserLogin",
  data() {
    return {
      username: "",
      password: "",
      rules: [
        (value) => {
          if (value) return true;
          return "Füllen Sie dieses Feld aus.";
        },
      ],
      data: null,
    };
  },
  methods: {
    async login() {
      await axios
        .get("http://localhost:8080/userinfo")
        .then((response) => {
          this.data = response.data;
        })
        .catch((error) => console.log(error));
      localStorage.setItem("user", this.data);
      this.$router.push({ name: "VacationCalendar" });
      //await axios
      //  .get(
      //    `http://localhost:8080/UserLogin?benutzername=${this.benutzername}&passwort=${this.passwort}`
      //  )
      //  .then((response) => console.log(response))
      //  .catch((error) => console.log(error));
      //  if(response.status == 200){
      //  localStorage.setItem("user",JSON.stringify(response.data[0]));

      //  }
      //  },
    },
  },
};
</script>