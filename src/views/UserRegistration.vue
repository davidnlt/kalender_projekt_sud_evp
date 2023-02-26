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
              <v-alert v-if="successMessage" type="success" text outlined>
                {{ successMessage }}
              </v-alert>
              <v-alert v-if="errorMessage" type="error" text outlined>
                {{ errorMessage }}
              </v-alert>
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

                  <v-select
                    v-model="department"
                    :items="items"
                    label="Abteilung"
                    outlined
                    rounded
                    required
                    color="black"
                  ></v-select>

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
                    :append-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="showPass ? 'text' : 'password'"
                    @click:append="showPass = !showPass"
                    color="black"
                    :rules="rules"
                    outlined
                    rounded
                  ></v-text-field>

                  <v-text-field
                    v-model="confirmPassword"
                    name="confirmPassword"
                    label="Passwort bestätigen"
                    :append-icon="showPassCon ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="showPassCon ? 'text' : 'password'"
                    @click:append="showPassCon = !showPassCon"
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
      departmentId: "",
      items: ["1. Anwendungsentwicklung", "2. Systemintegration"],
      confirmPassword: "",
      successMessage: "",
      errorMessage: "",
      showPass: false,
      showPassCon: false,
      rules: [
        (value) => {
          if (value) return true;
          return "Dies ist ein Pflichtfeld";
        },
      ],
    };
  },

  mounted() {
    this.checkAccess();
  },

  methods: {
    /**
     * Tokenprüfungs-Funktion:
     * Es wird überprüft, ob der Benutzer ein AccessToken besitzt. Dieses befindet sich im LocalStorage.
     * Liegt ein AccessToken vor, so wird der Benutzer auf die Urlaubskalender-Homepage weitergeleitet.
     *
     * @author David Nolte
     *
     * @return Der Benutzer wird auf die Urlaubskalender-Homepage weitergeleitet.
     */
    checkAccess() {
      var accessToken = localStorage.getItem("AccessToken");
      if (accessToken) {
        this.$router.push({ name: "VacationCalendar" });
      }
    },

    /**
     * API-Registrierungs-Funktion über ein POST-Request:
     * Bei erfolgreicher Eingabe der erforderlichen Benutzer-Daten wird ein neuer Benutzer-Account angelegt.
     *
     * @author David Nolte
     *
     * @param json (JSON-Datei, die den Benutzernamen, das Passwort, den Vornamen, den Nachnamen und die Abteilungs-Id enthält)
     *
     * @return Abhängig davon ob die Registrierung erfolgreich war oder nicht, wird die Antwort der API entweder in der Variablen 
     * successMessage oder errorMessage des Datenobjekts data() gespeichert. Mithilfe des Data-Bindings wird die Nachricht dann 
     * im Frontend ausgegeben.
     */
    async register() {
      this.departmentId = this.department.substr(0, 1);
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
            this.successMessage = response.data;
          })
          .catch((error) => {
            console.log(error);
            this.errorMessage = error.response.data;
          });
      } else {
        this.errorMessage = "Die Passwörter stimmen nicht überein!";
      }
    },
  },
};
</script>
