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
              <v-card-text>
                <form ref="form" @submit.prevent="register()">
                  <v-text-field
                    v-model="firstname"
                    name="firstname"
                    label="Vorname"
                    type="text"
                    color="black"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-text-field
                    v-model="surname"
                    name="surname"
                    label="Nachname"
                    type="text"
                    color="black"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-text-field
                    v-model="department"
                    name="department"
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
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    @click:append="show1 = !show1"
                    color="black"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-text-field
                    v-model="confirmPassword"
                    name="confirmPassword"
                    label="Passwort bestätigen"
                    :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show2 ? 'text' : 'password'"
                    @click:append="show2 = !show2"
                    color="black"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-btn type="submit" x-large block dark rounded class="mb-2"
                    >Registrieren</v-btn
                  >
                  <v-row>
                    <v-col class="text-right">
                      <v-btn to="/login" large color="black" text rounded>
                        Hast du bereits einen Account?
                      </v-btn>
                    </v-col>
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
  name: "UserRegistration",
  data() {
    return {
      username: "",
      password: "",
      firstname: "",
      surname: "",
      department: "",
      departmentId: "1",
      confirmPassword: "",
      show1: false,
      show2: false,
      rules: [
        (value) => {
          if (value) return true;
          return "Dies ist ein Pflichtfeld";
        },
      ],
    };
  },
  methods: {
    async register() {
      if (this.password == this.confirmPassword) {
        const json = JSON.stringify({
          username: `${this.username}`,
          password: `${this.password}`,
          firstname: `${this.firstname}`,
          surname: `${this.surname}`,
          department_id: `${this.departmentId}`,
        });
        await axios
          .post("http://localhost:8080/register", json, {
            headers: { "content-type": "application/json" },
          })
          .then((response) => {
            console.log(response);
            this.$router.push({ name: "UserLogin" });
          })
          .catch((error) => console.log(error));
      } else {
        alert("Passwort stimmt nicht überein");
      }
    },
  },
};
</script>
