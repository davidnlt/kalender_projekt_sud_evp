<template>
  <v-app>
    <CalendarHeader />
    <v-main>
      <v-row>
        <v-col>
          <v-sheet>
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
                >Zur Verfügung stehende Urlaubstage:
              </v-btn>

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
          <v-sheet height="700">
            <v-calendar
              ref="calendar"
              v-model="focus"
              color="primary"
              :events="events"
              :event-color="getEventColor"
              type="month"
              @click:event="showEvent"
              @change="updateRange"
            ></v-calendar>
            <v-menu
              v-model="selectedOpen"
              :close-on-content-click="false"
              :activator="selectedElement"
              offset-x
            >
              <v-card min-width="350px" flat>
                <v-toolbar :color="selectedEvent.color" dark>
                  <v-tool-title> {{ selectedEvent.name }}</v-tool-title>
                </v-toolbar>
                <v-card-text>
                  Von {{ selectedEvent.start }} bis
                  {{ selectedEvent.end }}</v-card-text
                >
                <v-card-actions>
                  <v-btn variant="text" @click="selectedOpen = false" rounded>
                    Schließen
                  </v-btn>
                  <v-btn variant="text" @click="selectedOpen = false" rounded>
                    Löschen
                  </v-btn>
                  <v-btn variant="text" @click="selectedOpen = false" rounded>
                    Änderung speichern
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-menu>
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
//import axios from "axios";

export default {
  name: "VacationCalendar",
  components: {
    CalendarHeader,
    CalendarFooter,
  },
  data: () => ({
    focus: "",
    selectedEvent: {},
    selectedElement: null,
    selectedOpen: false,
    events: [],
    colors: [
      "blue",
      "indigo",
      "deep-purple",
      "cyan",
      "green",
      "orange",
      "grey darken-1",
    ],
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
  }),
  mounted() {
    this.$refs.calendar.checkChange();
  },
  methods: {
    getEventColor(event) {
      return event.color;
    },

    setToday() {
      this.focus = "";
    },

    prev() {
      this.$refs.calendar.prev();
    },

    next() {
      this.$refs.calendar.next();
    },

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

    updateRange() {
      const events = [];

      for (let i = 0; i < 2; i++) {
        events.push({
          name: "Urlaub David Nolte",
          start: "2023-02-20",
          end: "2023-02-22",
          color: "blue",
        });
      }
      this.events = events;
    },

    addVacationEntry() {
      console.log(this.vacationStartDate);
      console.log(this.vacationEndDate);
    },
  },
};
</script>
