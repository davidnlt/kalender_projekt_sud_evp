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
              >
                Heute
              </v-btn>
              <v-btn fab small color="grey-darken" @click="prev">
                <v-icon size="small"> mdi-chevron-left </v-icon>
              </v-btn>
              <v-btn fab small color="grey-darken" @click="next">
                <v-icon size="small"> mdi-chevron-right </v-icon>
              </v-btn>
              <v-toolbar-title v-if="$refs.calendar">
                {{ $refs.calendar.title }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
              <v-menu location="bottom end">
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    variant="outlined"
                    color="grey-darken-2"
                    v-bind="attrs"
                    v-on="on"
                  >
                    <span>{{ typeToLabel[type] }}</span>
                    <v-icon end> mdi-menu-down </v-icon>
                  </v-btn>
                </template>
              </v-menu>

              <v-dialog v-model="dialog" persistent width="1024">
                <template v-slot:activator="{ props }">
                  <v-btn @click="dialog = true" v-bind="props">
                    Urlaubseintrag hinzufügen
                  </v-btn>
                </template>
                <v-card>
                  <v-card-title>
                    <span class="text-h5"> Urlaubseintrag hinzufügen</span>
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
                          Hinzufügen
                        </v-btn>
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
              :type="type"
              @click:event="showEvent"
              @click:more="viewDay"
              @click:date="viewDay"
              @change="updateRange"
            ></v-calendar>
            <v-menu
              v-model="selectedOpen"
              :close-on-content-click="false"
              :activator="selectedElement"
              offset-x
            >
              <v-card color="grey-lighten-4" min-width="350px" flat>
                <v-toolbar :color="selectedEvent.color" dark>
                  <v-btn icon>
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-toolbar-title
                    v-html="selectedEvent.name"
                  ></v-toolbar-title>
                  <v-spacer></v-spacer>
                  <v-btn icon>
                    <v-icon>mdi-heart</v-icon>
                  </v-btn>
                  <v-btn icon>
                    <v-icon>mdi-dots-vertical</v-icon>
                  </v-btn>
                </v-toolbar>
                <v-card-text>
                  <span v-html="selectedEvent.details"></span>
                </v-card-text>
                <v-card-actions>
                  <v-btn
                    variant="text"
                    color="secondary"
                    @click="selectedOpen = false"
                  >
                    Cancel
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

export default {
  name: "VacationCalendar",
  components: {
    CalendarHeader,
    CalendarFooter,
  },
  data: () => ({
    focus: "",
    type: "month",
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
    names: ["Urlaub"],
    dialog: false,
  }),
  mounted() {
    this.$refs.calendar.checkChange();
  },
  methods: {
    viewDay({ date }) {
      this.focus = date;
      this.type = "day";
    },
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
    updateRange({ start, end }) {
      const events = [];
      const min = new Date(`${start.date}T00:00:00`);
      const max = new Date(`${end.date}T23:59:59`);
      const days = (max.getTime() - min.getTime()) / 86400000;
      const eventCount = this.rnd(days, days + 20);
      for (let i = 0; i < eventCount; i++) {
        const allDay = this.rnd(0, 3) === 0;
        const firstTimestamp = this.rnd(min.getTime(), max.getTime());
        const first = new Date(firstTimestamp - (firstTimestamp % 900000));
        const secondTimestamp = this.rnd(2, allDay ? 288 : 8) * 900000;
        const second = new Date(first.getTime() + secondTimestamp);
        events.push({
          name: this.names[this.rnd(0, this.names.length - 1)],
          start: first,
          end: second,
          color: this.colors[this.rnd(0, this.colors.length - 1)],
          timed: !allDay,
        });
      }
      this.events = events;
    },
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a;
    },
  },
};
</script>
