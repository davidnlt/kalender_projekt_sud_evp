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
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    @click:append="show1 = !show1"
                    color="black"
                    prepend-inner-icon="mdi-lock"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-btn type="submit" x-large block dark rounded class="mb-2"
                    >Anmelden</v-btn
                  >

                  <v-row>
                    <v-dialog v-model="dialog" persistent width="600">
                      <template v-slot:activator="{ props }">
                        <v-col class="text-left">
                          <v-btn
                            @click="dialog = true"
                            v-bind="props"
                            color="black"
                            text
                            rounded
                            >Passwort vergessen?
                          </v-btn>
                        </v-col>
                        <v-col class="text-right">
                          <v-btn
                            to="/registration"
                            large
                            color="black"
                            text
                            rounded
                          >
                            Registrieren
                          </v-btn>
                        </v-col>
                      </template>
                      <v-card>
                        <v-card-title>
                          <span class="text-h5"> Passwort zurücksetzen </span>
                        </v-card-title>
                        <v-card-text>
                          <v-container>
                            <form ref="form" @submit.prevent="register()">
                              <p>
                                Bitte gib deine E-Mail-Adresse ein. So können
                                wir dir einen Link zum Zurücksetzen schicken.
                              </p>

                              <v-text-field
                                v-model="email"
                                name="email"
                                label="E-Mail-Adresse"
                                type="email"
                                color="black"
                                outlined
                                rounded
                              ></v-text-field>
                              <v-row
                                ><v-col
                                  ><v-btn
                                    @click="dialog = false"
                                    large
                                    dark
                                    rounded
                                  >
                                    Schließen
                                  </v-btn></v-col
                                >
                                <v-col
                                  ><v-btn
                                    type="submit"
                                    @click="dialog = false"
                                    large
                                    dark
                                    rounded
                                  >
                                    Link zum Zurücksetzen erhalten
                                  </v-btn></v-col
                                >
                              </v-row>
                            </form>
                          </v-container>
                        </v-card-text>
                      </v-card>
                    </v-dialog>
                  </v-row>
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
      email: "",
      show1: false,
      rules: [
        (value) => {
          if (value) return true;
          return "Dies ist ein Pflichtfeld";
        },
      ],
      data: "",
      dialog: false,
    };
  },
  methods: {
    async login() {
      //console.log(this.username.substr(0, 2));
      const json = JSON.stringify({
        username: `${this.username}`,
        password: `${this.password}`,
      });
      await axios
        .post("http://localhost:8080/login", json, {
          headers: { "content-type": "application/json" },
        })
        .then((response) => {
          console.log(response);
          localStorage.setItem("AccessToken", response.data.accessToken);
          localStorage.setItem("Username", this.username);
          localStorage.setItem("Password", this.password);
          this.$router.push({ name: "VacationCalendar" });
        })
        .catch((error) => {
          console.log(error);
          console.log(error.response.data.message);
        });
    },
  },
};
</script>
