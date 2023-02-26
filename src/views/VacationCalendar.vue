<template>
  <v-app>
    <CalendarHeader />
    <v-main>
      <v-row>
        <v-col>
          <v-sheet>
            <!-- Navbar -->
            <v-alert v-if="successMessage" type="success" text outlined>
              {{ successMessage }}
            </v-alert>
            <v-alert v-if="errorMessage" type="error" text outlined>
              {{ errorMessage }}
            </v-alert>
            <v-toolbar flat>
              <v-btn
                variant="outlined"
                class="me-4"
                color="grey-darken-2"
                @click="setToday"
                rounded
              >
                Heute
              </v-btn>
              <v-btn fab small color="grey-darken" @click="prev">
                <v-icon size="small"> mdi-chevron-left </v-icon>
              </v-btn>
              <v-btn fab small color="grey-darken" @click="next">
                <v-icon size="small"> mdi-chevron-right </v-icon>
              </v-btn>
              <v-toolbar-title v-if="$refs.calendar" class="ml-4">
                {{ $refs.calendar.title }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
              <v-btn rounded color="primary"
                >Zur Verfügung stehende Urlaubstage: {{ holidaysRemaining }}
              </v-btn>
              <v-spacer></v-spacer>
              <v-dialog v-model="dialog" persistent width="500">
                <template v-slot:activator="{ props }">
                  <v-btn @click="dialog = true" v-bind="props" rounded>
                    Urlaubseintrag hinzufügen
                  </v-btn>
                </template>
                <v-card>
                  <v-card-title>
                    <span class="text-h5"> Urlaubseintrag hinzufügen</span>
                  </v-card-title>
                  <v-card-text>
                    <v-alert
                      v-if="errorMessageAddEntry"
                      type="error"
                      text
                      outlined
                    >
                      {{ errorMessageAddEntry }}
                    </v-alert>
                    <v-container>
                      <form ref="form" @submit.prevent="addVacationEntry()">
                        <v-row>
                          <v-col cols="12" lg="6">
                            <v-menu
                              ref="startdate_picker"
                              v-model="startdate_picker"
                              :close-on-content-click="false"
                              :return-value.sync="vacationStartDate"
                              transition="scale-transition"
                              offset-y
                              min-width="auto"
                            >
                              <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                  v-model="vacationStartDate"
                                  label="Startdatum"
                                  prepend-icon="mdi-calendar"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-date-picker
                                v-model="vacationStartDate"
                                no-title
                                scrollable
                              >
                                <v-spacer></v-spacer>
                                <v-btn
                                  text
                                  color="primary"
                                  @click="startdate_picker = false"
                                  rounded
                                >
                                  Abbrechen
                                </v-btn>
                                <v-btn
                                  text
                                  color="primary"
                                  @click="
                                    $refs.startdate_picker.save(
                                      vacationStartDate
                                    )
                                  "
                                  rounded
                                >
                                  OK
                                </v-btn>
                              </v-date-picker>
                            </v-menu>
                          </v-col>

                          <v-col cols="12" lg="6">
                            <v-menu
                              ref="enddate_picker"
                              v-model="enddate_picker"
                              :close-on-content-click="false"
                              :return-value.sync="vacationEndDate"
                              transition="scale-transition"
                              offset-y
                              min-width="auto"
                            >
                              <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                  v-model="vacationEndDate"
                                  label="Enddatum"
                                  prepend-icon="mdi-calendar"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-date-picker
                                v-model="vacationEndDate"
                                no-title
                                scrollable
                              >
                                <v-spacer></v-spacer>
                                <v-btn
                                  text
                                  color="primary"
                                  @click="enddate_picker = false"
                                  rounded
                                >
                                  Abbrechen
                                </v-btn>
                                <v-btn
                                  text
                                  color="primary"
                                  @click="
                                    $refs.enddate_picker.save(vacationEndDate)
                                  "
                                  rounded
                                >
                                  OK
                                </v-btn>
                              </v-date-picker>
                            </v-menu>
                          </v-col>
                        </v-row>
                        <v-row
                          ><v-col class="text-left"
                            ><v-btn
                              variant="text"
                              @click="dialog = false"
                              rounded
                            >
                              Schließen
                            </v-btn></v-col
                          ><v-col class="text-right"
                            ><v-btn
                              type="submit"
                              variant="text"
                              @click="dialog = false"
                              rounded
                            >
                              Hinzufügen
                            </v-btn></v-col
                          ></v-row
                        >
                      </form>
                    </v-container>
                  </v-card-text>
                </v-card>
              </v-dialog>
            </v-toolbar>
          </v-sheet>

          <v-sheet height="650" class="ma-4" rounded>
            <!-- Urlaubskalender -->
            <v-calendar
              ref="calendar"
              v-model="focus"
              color="primary"
              :events="events"
              type="month"
              @click:event="showEvent"
            ></v-calendar>
            <!-- /Urlaubskalender -->

            <!-- Bestehender Urlaubseintrag bearbeiten -->
            <v-menu
              v-model="selectedOpen"
              :close-on-content-click="false"
              :activator="selectedElement"
              offset-x
            >
              <v-card width="auto">
                <v-toolbar :color="selectedEvent.color" dark class="text-center"
                  ><v-spacer />
                  <v-tool-title>
                    {{ selectedEvent.name }}
                  </v-tool-title>
                  <v-spacer
                /></v-toolbar>

                <v-card-text>
                  <v-alert
                    v-if="errorMessageUpdateEvent"
                    type="error"
                    text
                    outlined
                  >
                    {{ errorMessageUpdateEvent }}
                  </v-alert>
                  <v-row>
                    <v-col cols="12" lg="6">
                      <v-menu
                        ref="startdate_picker"
                        v-model="startdate_picker"
                        :close-on-content-click="false"
                        :return-value.sync="vacationStartDateUpdate"
                        transition="scale-transition"
                        offset-y
                        min-width="auto"
                      >
                        <template v-slot:activator="{ on, attrs }">
                          <v-text-field
                            v-if="updateStart"
                            v-model="vacationStartDateUpdate"
                            label="Startdatum"
                            prepend-icon="mdi-calendar"
                            readonly
                            v-bind="attrs"
                            v-on="on"
                          ></v-text-field>
                          <v-text-field
                            v-else
                            :value="selectedEvent.start"
                            label="Startdatum"
                            prepend-icon="mdi-calendar"
                            readonly
                            v-bind="attrs"
                            v-on="on"
                          ></v-text-field>
                        </template>
                        <v-date-picker
                          v-model="vacationStartDateUpdate"
                          no-title
                          scrollable
                        >
                          <v-spacer></v-spacer>
                          <v-btn
                            text
                            color="primary"
                            @click="startdate_picker = false"
                            rounded
                          >
                            Abbrechen
                          </v-btn>
                          <v-btn
                            text
                            color="primary"
                            @click="
                              $refs.startdate_picker.save(
                                vacationStartDateUpdate
                              );
                              updateStart = true;
                            "
                            rounded
                          >
                            OK
                          </v-btn>
                        </v-date-picker>
                      </v-menu>
                    </v-col>

                    <v-col cols="12" lg="6">
                      <v-menu
                        ref="enddate_picker"
                        v-model="enddate_picker"
                        :close-on-content-click="false"
                        :return-value.sync="vacationEndDateUpdate"
                        transition="scale-transition"
                        offset-y
                        min-width="auto"
                      >
                        <template v-slot:activator="{ on, attrs }">
                          <v-text-field
                            v-if="updateEnd"
                            v-model="vacationEndDateUpdate"
                            label="Enddatum"
                            prepend-icon="mdi-calendar"
                            readonly
                            v-bind="attrs"
                            v-on="on"
                          ></v-text-field>
                          <v-text-field
                            v-else
                            :value="selectedEvent.end"
                            label="Enddatum"
                            prepend-icon="mdi-calendar"
                            readonly
                            v-bind="attrs"
                            v-on="on"
                          ></v-text-field>
                        </template>
                        <v-date-picker
                          v-model="vacationEndDateUpdate"
                          no-title
                          scrollable
                        >
                          <v-spacer></v-spacer>
                          <v-btn
                            text
                            color="primary"
                            @click="enddate_picker = false"
                            rounded
                          >
                            Abbrechen
                          </v-btn>
                          <v-btn
                            text
                            color="primary"
                            @click="
                              $refs.enddate_picker.save(vacationEndDateUpdate);
                              updateEnd = true;
                            "
                            rounded
                          >
                            OK
                          </v-btn>
                        </v-date-picker>
                      </v-menu>
                    </v-col>
                  </v-row>
                </v-card-text>
                <v-card-actions>
                  <v-btn
                    variant="text"
                    @click="
                      (selectedOpen = false),
                        (updateStart = false),
                        (vacationStartDateUpdate = null),
                        (updateEnd = false),
                        (vacationEndDateUpdate = null)
                    "
                    rounded
                  >
                    Schließen
                  </v-btn>
                  <v-spacer></v-spacer>
                  <v-btn
                    variant="text"
                    @click.prevent="
                      updateEvent(
                        selectedEvent.id,
                        vacationStartDateUpdate,
                        vacationEndDateUpdate
                      )
                    "
                    rounded
                  >
                    Änderungen speichern
                  </v-btn>
                  <v-btn
                    variant="text"
                    @click="deleteEvent(selectedEvent.id)"
                    rounded
                  >
                    Löschen
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-menu>
            <!-- /Bestehender Urlaubseintrag bearbeiten -->
          </v-sheet>
        </v-col>
      </v-row>
    </v-main>
    <CalendarFooter />
  </v-app>
</template>

<script>
import CalendarHeader from "../components/CalendarHeader.vue";
import CalendarFooter from "../components/CalendarFooter.vue";
import axios from "axios";

export default {
  name: "VacationCalendar",

  components: {
    CalendarHeader,
    CalendarFooter,
  },

  data: () => ({
    firstname: "",
    surname: "",
    department: "",
    holidaysRemaining: "",
    holidayTotal: "",
    focus: "",
    selectedEvent: {},
    selectedElement: null,
    selectedOpen: false,
    events: [],
    dialog: false,
    vacationStartDate: new Date(
      Date.now() - new Date().getTimezoneOffset() * 60000
    )
      .toISOString()
      .substr(0, 10),
    vacationEndDate: new Date(
      Date.now() - new Date().getTimezoneOffset() * 60000
    )
      .toISOString()
      .substr(0, 10),
    startdate_picker: false,
    enddate_picker: false,
    entry_id: 0,
    color: "blue",
    vacationStartDateUpdate: null,
    updateStart: false,
    vacationEndDateUpdate: null,
    updateEnd: false,
    successMessage: "",
    errorMessage: "",
    errorMessageAddEntry: "",
    errorMessageUpdateEvent: "",
  }),

  created() {
    this.checkAccess();
  },

  mounted() {
    this.$refs.calendar.checkChange();

    this.getUserInfo();

    this.getEvents();
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
      } else {
        this.$router.push({ name: "UserLogin" });
      }
    },

    /**
     * API-Zugriffs-Funktion über ein GET-Request:
     * Auslesen von Benutzerinformationen des jeweils angemeldeten Benutzers
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     *
     * @return Die Antwort der API wird in den Eigenschaften (firstname, surname, department,
     * holidays.Remaining, holidayTotal) des Datenobjekts data() gespeichert. Mithilfe dieses
     * Bindings stehen die Benutzerdaten der gesamten Komponente zur Verfügung.
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
          this.holidaysRemaining = response.data[0].holidays_remaining;
          this.holidayTotal = response.data[0].holiday_total;
        })
        .catch((error) => {
          console.log(error);
        });
    },

    /**
     * API-Zugriffs-Funktion über ein GET-Request:
     * Auslesen aller Urlaubseinträge der Abteilung, welcher der Benutzer angehört.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     *
     * @return Die sendet als Antwort eine JSON-Datei mit allen Urlaubseinträgen einer Abteilung
     * (abhängig von der Zugehörigkeit des Benutzers). Jeder Urlaubseintrag wird als Objekt im
     * Array events[] gespeichert.
     */
    async getEvents() {
      let vacationEntries = [];
      let events = [];
      await axios
        .get("http://localhost:8080/alldataentries", {
          headers: { Authorization: localStorage.getItem("AccessToken") },
        })
        .then((response) => {
          vacationEntries = response.data;
          vacationEntries.forEach((entry) => {
            let entryData = [];
            entryData.id = entry.entry_id;
            entryData.name = "Urlaub " + entry.firstname + " " + entry.surname;
            entryData.start = entry.startdate;
            entryData.end = entry.enddate;
            entryData.color = "blue";
            events.push(entryData);
          });
          this.events = events;
        })
        .catch((error) => {
          console.log(error);
          this.errorMessage = error.response.data.message;
        });
    },

    /**
     * Funktion:
     * Bildet den Monat des aktuellen Tages ab und markiert diesen.
     */
    setToday() {
      this.focus = "";
    },

    /**
     * Funktion:
     * Bildet den voherigen Monat ab.
     */
    prev() {
      this.$refs.calendar.prev();
    },

    /**
     * Funktion:
     * Bildet den nächsten Monat ab.
     */
    next() {
      this.$refs.calendar.next();
    },

    /**
     * Funktion:
     * Öffnet ein Dialog-Fenster, wenn man auf einen bestehenden Urlaubseintrag klickt.
     * Dadurch besitzt der Benutzer die Möglichkeit, notwendige Anpassungen an seinem
     * Urlaubseintrag vorzunehmen.
     *
     * @param nativeEvent
     * @param event
     *
     * @return selectedOpen = true
     */
    showEvent({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event;
        this.selectedElement = nativeEvent.target;
        requestAnimationFrame(() =>
          requestAnimationFrame(() => (this.selectedOpen = true))
        );
      };
      if (this.selectedOpen) {
        this.selectedOpen = false;
        requestAnimationFrame(() => requestAnimationFrame(() => open()));
      } else {
        open();
      }
      nativeEvent.stopPropagation();
    },

    /**
     * API-Zugriffs-Funktion über ein PUT-Request:
     * Update bestehender Urlaubseinträge. Das Start- und Enddatum können individuell verändert werden.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     * @param json (JSON-Datei, die die Urlaubseintrag-Id, das (veränderte) Startdatum und das (veränderte) Enddatum enthält)
     *
     * @return Abhängig davon ob die Bearbeitung erfolgreich war oder nicht, wird die Antwort der API entweder in der Variablen
     * successMessage oder errorMessage des Datenobjekts data() gespeichert. Mithilfe des Data-Bindings wird die Nachricht dann
     * im Frontend ausgegeben.
     */
    async updateEvent(entryId, start, end) {
      const json = JSON.stringify({
        entry_id: `${entryId}`,
        startdate: `${start}`,
        enddate: `${end}`,
      });
      await axios
        .put(`http://localhost:8080/entry/update`, json, {
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
          this.errorMessageUpdateEvent = error.response.data;
        });
    },

    /**
     * API-Zugriffs-Funktion über ein DELETE-Request:
     * Löschung eines Urlaubseintrages des Benutzers.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     * @param entryId
     *
     * @return Abhängig davon ob die Löschung erfolgreich war oder nicht, wird die Antwort der API entweder in der Variablen
     * successMessage oder errorMessage des Datenobjekts data() gespeichert. Mithilfe des Data-Bindings wird die Nachricht dann
     * im Frontend ausgegeben.
     */
    async deleteEvent(entryId) {
      await axios
        .delete(`http://localhost:8080/entry/delete/${entryId}`, {
          headers: {
            Authorization: localStorage.getItem("AccessToken"),
          },
        })
        .then((response) => {
          console.log(response);
          window.location.reload();
        })
        .catch((error) => {
          console.log(error);
          this.errorMessageUpdateEvent = error.response.data;
        });
    },

    /**
     * API-Zugriffs-Funktion über ein POST-Request:
     * Speicherung eines neuen Urlaubseintrages des Benutzers.
     *
     * @author David Nolte
     *
     * @param AccessToken (eindeutige Identifikation des Benutzers)
     * @param json (JSON-Datei, die die Urlaubseintrag-Id, das Startdatum und das Enddatum enthält)
     *
     * @return Abhängig davon ob das Hinzufügen eines Urlaubseintrages erfolgreich war oder nicht, wird die Antwort der API entweder
     * in der Variablen successMessage oder errorMessage des Datenobjekts data() gespeichert. Mithilfe des Data-Bindings wird die
     * Nachricht dann im Frontend ausgegeben.
     */
    async addVacationEntry() {
      const json = JSON.stringify({
        entry_id: `${this.entry_id}`,
        startdate: `${this.vacationStartDate}`,
        enddate: `${this.vacationEndDate}`,
      });
      await axios
        .post("http://localhost:8080/entry/save", json, {
          headers: {
            Authorization: localStorage.getItem("AccessToken"),
            "content-type": "application/json",
          },
        })
        .then((response) => {
          console.log(response);
          window.location.reload();
        })
        .catch((error) => {
          console.log(error);
          this.errorMessageAddEntry = error.response.data;
        });
    },
  },
};
</script>
