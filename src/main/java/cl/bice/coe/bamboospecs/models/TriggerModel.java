/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.bice.coe.bamboospecs.models;

import cl.bice.coe.bamboospecs.models.enums.TriggerType;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.atlassian.bamboo.specs.builders.trigger.ScheduledTrigger;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TriggerModel {
    @NotNull
    public TriggerType type;

    @NotNull
    @NotEmpty
    public String description;

    public String everyNumHours;
    public String dailyAt;
    public String weeklyAt;
    public String monthlyAt;
    public String cron;
    public String branch;


    static private LocalTime parseTimeNicely(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
    }

    public Trigger asTrigger() {
        switch (this.type) {
            case AFTER_SUCCESSFUL_BUILD_PLAN:
                AfterSuccessfulBuildPlanTrigger buildTrigger = new AfterSuccessfulBuildPlanTrigger();
                buildTrigger.description(this.description);
                if (this.branch != null) {
                    buildTrigger.triggerByBranch(this.branch);
                }
                return buildTrigger;
            case AFTER_STASH_COMMIT:
                return new BitbucketServerTrigger().description(this.description);
            case SCHEDULED:
                ScheduledTrigger scheduledTrigger = new ScheduledTrigger().description(this.description);
                if (this.everyNumHours != null) {
                    scheduledTrigger.scheduleEvery(Integer.parseInt(this.everyNumHours), TimeUnit.HOURS);
                }
                if (this.dailyAt != null) {
                    scheduledTrigger.scheduleOnceDaily(parseTimeNicely(dailyAt));
                }
                if (this.weeklyAt != null) {
                    String[] parts = this.weeklyAt.split(" ");
                    String upperDay = parts[0].toUpperCase();
                    scheduledTrigger.scheduleWeekly(parseTimeNicely(parts[1]), DayOfWeek.valueOf(upperDay));
                }
                if (this.monthlyAt != null) {
                    String[] parts = this.monthlyAt.split(" ");
                    scheduledTrigger.scheduleMonthly(parseTimeNicely(parts[1]), Integer.getInteger(parts[0]));
                }
                if (this.cron != null) {
                    scheduledTrigger.cronExpression(this.cron);
                }
                return scheduledTrigger;
            default:
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }
}
