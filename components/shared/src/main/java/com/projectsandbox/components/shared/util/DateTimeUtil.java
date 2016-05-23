package com.projectsandbox.components.shared.util;

import org.joda.time.*;

/**
 * Created by limpygnome on 07/08/15.
 */
public class DateTimeUtil
{
    
    public static String humanTimeSince(DateTime start, DateTime end)
    {
        int seconds = Seconds.secondsBetween(start, end).getSeconds();

        if (seconds <= 1)
        {
            return "1 second ago";
        }
        else if (seconds < 60)
        {
            return seconds + " seconds ago";
        }
        else
        {
            int minutes = Minutes.minutesBetween(start, end).getMinutes();

            if (minutes <= 1)
            {
                return "1 minute ago";
            }
            else if (minutes < 60)
            {
                return minutes + " minutes ago";
            }
            else
            {
                int hours = Hours.hoursBetween(start, end).getHours();

                if (hours <= 1)
                {
                    return "1 hour ago";
                }
                else if (hours < 24)
                {
                    return hours + " hours ago";
                }
                else
                {
                    int days = Days.daysBetween(start, end).getDays();

                    if (days <= 1)
                    {
                        return "1 day";
                    }
                    else if (days < 28)
                    {
                        return days + " days ago";
                    }
                    else
                    {
                        int months = Months.monthsBetween(start, end).getMonths();

                        if (months <= 1)
                        {
                            return "1 month ago";
                        }
                        else if (months < 12)
                        {
                            return months + " months ago";
                        }
                        else
                        {
                            int years = Years.yearsBetween(start, end).getYears();

                            return years + " years ago";
                        }
                    }
                }
            }
        }
    }
    
}
