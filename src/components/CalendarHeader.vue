<template>
  <v-toolbar color="white">
    <v-toolbar-title class="text-center font-weight-bold">
      {{ department }} - Urlaubskalender
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-alert v-if="successMessageUpdateUser" type="error" text outlined>
      {{ successMessageUpdateUser }}
    </v-alert>
    <v-alert v-if="errorMessageUpdateUser" type="error" text outlined>
      {{ errorMessageUpdateUser }}
    </v-alert>
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
          <v-card-title> Benutzerprofil bearbeiten </v-card-title>
          <v-alert v-if="errorMessageUpdateUser" type="error" text outlined>
            {{ errorMessageUpdateUser }}
          </v-alert>
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
                  :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                  :type="show ? 'text' : 'password'"
                  @click:append="show = !show"
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
  </v-toolbar>
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
      show: "",
      rules: [
        (value) => {
          if (value) return true;
          return "Dies ist ein Pflichtfeld";
        },
      ],
      successMessageUpdateUser: "",
      errorMessageUpdateUser: "",
    };
  },

  created() {
    this.getUserInfo();
  },

  methods: {
    /**
     * API-Zugriffs-Funktion über ein GET-Request:
     * Auslesen von Benutzerinformationen des jeweils angemeldeten Benutzers.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     *
     * @return Die Antwort der API wird in den Eigenschaften (firstname, surname, department)
     * des Datenobjekts data() gespeichert. Mithilfe dieses Bindings stehen die Benutzerdaten
     * der gesamten Komponente zur Verfügung.
     */
    async getUserInfo() {
      await axios
        .get("http://localhost:8080/userinfo", {
          headers: { Authorization: localStorage.getItem("AccessToken") },
        })
        .then((response) => {
          console.log(response);
          this.firstname = response.data[0].firstname;
          this.surname = response.data[0].surname;
          this.department = response.data[0].department;
          this.username = localStorage.getItem("Username");
          this.password = localStorage.getItem("Password");
        })
        .catch((error) => {
          console.log(error);
        });
    },

    /**
     * API-Zugriffs-Funktion über ein POST-Request:
     * Speicherung von vorgenommen Benutzerdaten-Änderungen des jeweils angemeldeten Benutzers.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     * @param json (JSON-Datei, die den Vornamen, den Nachnamen und das Passwort enthält)
     *
     * @return Die Antwort der API wird in der Variablen message des Datenobjekts data() gespeichert.
     */
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
          this.successMessageUpdateUser = response.data;
        })
        .catch((error) => {
          console.log(error);
          this.errorMessageUpdateUser = error.response.data.message;
        });
    },

    /**
     * Abmeldungs-Funktion:
     * Das eindeutige AccessToken des angemeldeten Benutzers wird aus seinem LocalStorage entfernt.
     * Somit kann der Benutzer nicht mehr auf die Urlaubskalender-Homepage geleitet werden.
     *
     * @author David Nolte
     *
     * @return Der Benutzer wird auf die Login-Page umgeleitet.
     */
    logout() {
      localStorage.clear();
      this.$router.push({ name: "UserLogin" });
    },
  },
};
</script>
