/*
 *  Copyright 2013-2014 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.threeten.tzdiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.zone.ZoneOffsetTransition;
import org.threeten.bp.zone.ZoneRules;
import org.threeten.bp.zone.ZoneRulesProvider;

/**
 * Calculates changes to standard offsets.
 */
public class TzStandard {

    public static void main(String[] args) throws Exception {
        System.out.println(ZoneRulesProvider.getVersions("Europe/London").firstKey());
        System.out.println();
        
        
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        zoneIds.stream()
            .filter(zone -> zone.startsWith("US/") == false &&
                zone.contains("/") &&
                zone.startsWith("Etc/") == false &&
                zone.startsWith("SystemV/") == false)
            .sorted()
            .map(ZoneId::of)
            .forEach(TzStandard::calculate);
      }

      static void calculate(ZoneId zoneId) {
        ZoneRules rules = zoneId.getRules();
        ZoneOffset farPast = rules.getStandardOffset(Instant.MIN);
        ZoneOffset farFuture = rules.getStandardOffset(Instant.MAX);
        
        List<ZoneOffsetTransition> transitions = rules.getTransitions();
        List<ZoneOffsetTransition> standards = new ArrayList<>();
        ZoneOffsetTransition last = null;
        for (ZoneOffsetTransition zot : transitions) {
          ZoneOffset std = rules.getStandardOffset(zot.getInstant());
          if (last != null) {
            ZoneOffset lastStd = rules.getStandardOffset(last.getInstant());
            if (std.equals(lastStd) == false) {
              standards.add(ZoneOffsetTransition.of(zot.getDateTimeBefore(), lastStd, std));
              last = zot;
            }
          } else {
            if (zot.getOffsetBefore().equals(std) == false) {
              standards.add(ZoneOffsetTransition.of(zot.getDateTimeBefore(), zot.getOffsetBefore(), std));
              last = zot;
            }
          }
        }
        
        System.out.println(zoneId);
        System.out.println(farPast);
//        transitions.forEach(System.out::println);
//        System.out.println();
//        standards.forEach(zot -> System.out.println(zot + " " + zot.getDuration()));
        standards.forEach(zot -> System.out.println(String.format("%-10s  %s", zot.getOffsetAfter().toString(), zot)));
        System.out.println();
        
        
//        SortedMap<Instant, ZoneOffset> trans = new TreeMap<>(transitions.stream()
//            .collect(Collectors.toMap(ZoneOffsetTransition::getInstant, zot -> rules.getStandardOffset(zot.getInstant()))));
//        trans.put(ZonedDateTime.of(2015, 1, 1, 0, 0, 0, 0, farFuture).toInstant(), farFuture);
    //    
//        Map<ZoneOffset, Duration> map = new HashMap<>();
//        Instant lastIn = null;
//        ZoneOffset lastZO = null;
//        for (Map.Entry<Instant, ZoneOffset> entry : trans.entrySet()) {
//          if (lastIn != null) {
//            Duration dur = Duration.between(lastIn, entry.getKey());
//            map.merge(lastZO, dur, Duration::plus);
//          }
//          lastIn = entry.getKey();
//          lastZO = entry.getValue();
//        }
    //    
//        System.out.println(zoneId);
//        System.out.println(farPast);
//        transitions.forEach(System.out::println);
//        trans.forEach((a, b) -> System.out.println(a +" " + b));
//        map.forEach((a, b) -> System.out.println(a +" " + Period.ofDays((int) b.toDays())));
//        System.out.println();
    //
////        int diff = Math.abs(farFuture.getTotalSeconds() - farPast.getTotalSeconds());
////        if (diff > 3600) {
////          int hours = diff / 3600;
////          int minutes = (diff % 3600) / 60;
////          int seconds = diff % 60;
////          System.out.println(zoneId);
////          System.out.println(farPast);
//////          transitions.forEach(System.out::println);
////          System.out.println(farFuture);
////          map.forEach((offset, dur) -> {
////            System.out.println(offset + " " + Period.ofDays((int) dur.toDays()));
////          });
////          System.out.println(hours + ":"  + minutes + ":"  + seconds);
////          System.out.println();
////        }
    //
    }

}
