<template>
  <v-app-bar color="white">
    <v-toolbar-title>
      Willkommen {{ firstname }} im "Abteilung-Urblaubskalender"
    </v-toolbar-title>

    <v-spacer></v-spacer>
    <v-toolbar-item>
      <v-dialog v-model="dialog" persistent width="500">
        <template v-slot:activator="{ props }">
          <v-btn
            @click="dialog = true"
            v-bind="props"
            color="black"
            text
            rounded
            ><v-icon>mdi-account-edit</v-icon> Dein Konto
          </v-btn>
        </template>
        <v-card>
          <v-card-title>
            <span class="text-h5">Benutzerprofil bearbeiten</span>
          </v-card-title>
          <v-card-text>
            <v-container>
              <form ref="form" @submit.prevent="updateUser()">
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
                  name="nachname"
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
                  disabled
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
                  disabled
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
                <v-row
                  ><v-col
                    ><v-btn variant="text" @click="dialog = false" rounded>
                      Schließen
                    </v-btn>
                  </v-col>
                  <v-col>
                    <v-btn
                      type="submit"
                      variant="text"
                      @click="dialog = false"
                      rounded
                    >
                      Änderungen speichern
                    </v-btn></v-col
                  ></v-row
                >
              </form>
            </v-container>
          </v-card-text>
        </v-card>
      </v-dialog>

      <v-btn @click="logout()" color="black" text rounded>
        <v-icon>mdi-logout</v-icon>
        Abmelden
      </v-btn>
    </v-toolbar-item>
  </v-app-bar>
</template>

<script>
import axios from "axios";
export default {
  name: "CalendarHeader",
  data() {
    return {
      firstname: "",
      surname: "",
      department: "",
      username: "",
      password: "",
      dialog: false,
      show1: "",
      rules: [
        (value) => {
          if (value) return true;
          return "Dies ist ein Pflichtfeld";
        },
      ],
    };
  },
  async created() {
    await axios
      .get("http://localhost:8080/userinfo", {
        headers: { Authorization: localStorage.getItem("AccessToken") },
      })
      .then((response) => {
        console.log(response);
        localStorage.setItem("Surname", response.data[0].surname);
        localStorage.setItem("Firstname", response.data[0].firstname);
        localStorage.setItem("Department", response.data[0].department);
        this.firstname = localStorage.getItem("Firstname");
        this.surname = localStorage.getItem("Surname");
        this.department = localStorage.getItem("Department");
        this.username = localStorage.getItem("Username");
        this.password = localStorage.getItem("Password");
      })
      .catch((error) => {
        console.log(error);
        console.log(error.response.data.message);
      });
  },

  methods: {
    async updateUser() {
      const json = JSON.stringify({
        firstname: `${this.firstname}`,
        surname: `${this.surname}`,
        password: `${this.password}`,
      });
      await axios
        .post("http://localhost:8080/user/update", json, {
          headers: {
            Authorization: localStorage.getItem("AccessToken"),
            "content-type": "application/json",
          },
        })
        .then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.log(error);
          console.log(error.response.data.message);
        });
    },
    logout() {
      localStorage.clear();
      this.$router.push({ name: "UserLogin" });
    },
  },
};
</script>
