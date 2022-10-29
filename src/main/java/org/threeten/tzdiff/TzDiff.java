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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.threeten.bp.Instant;
import org.threeten.bp.zone.ZoneOffsetTransition;
import org.threeten.bp.zone.ZoneOffsetTransitionRule;
import org.threeten.bp.zone.ZoneRules;
import org.threeten.bp.zone.ZoneRulesProvider;

/**
 * Calculates changes to time-zones.
 */
public class TzDiff {

    public static void main(String[] args) throws Exception {
        // also change the jar file in the Eclipse build properties
        // ideally that is the "all" jar file thatr is output from threetenbp
        // note that you need to use the rearguard file in threetenbp to get the expected outcome
        String outputVersion = "2022fgtz-rearguard";
        List<String> ids = new ArrayList<>(ZoneRulesProvider.getAvailableZoneIds());
        ids.sort(Comparator.naturalOrder());
        ids.remove("Eire");
        ids.add("Eire");
        for (String id : ids) {
            if (id.startsWith("Europe/K")) {
                System.out.println("");
            }
            String name = id.replace('/', '-') + ".txt";
            File file = new File("/dev-oss/tzdiff/data", name);
            NavigableMap<String, ZoneRules> versions = ZoneRulesProvider.getVersions(id);
            ZoneRules rules = versions.get(outputVersion);
            if (rules != null) {
                System.out.println(file);
                try (FileWriter out = new FileWriter(file)) {
                    SortedSet<String> sameAs = findSame(id, rules, ids, outputVersion);
                    if (sameAs.isEmpty()) {
                        out.write("LMT: " + rules.getOffset(Instant.MIN));
                        out.write("\r\n");
                        for (ZoneOffsetTransition trans : rules.getTransitions()) {
                            out.write(trans.toString());
                            out.write("\r\n");
                        }
                        for (ZoneOffsetTransitionRule trans : rules.getTransitionRules()) {
                            out.write(trans.toString());
                            out.write("\r\n");
                        }
                    } else {
                        out.write("Zone is same as:\r\n");
                        for (String same : sameAs) {
                            out.write(same + "\r\n");
                        }
                    }
                }
            }
        }
    }

    private static SortedSet<String> findSame(String id, ZoneRules rules, List<String> otherIds, String version) {
        SortedSet<String> sameAs = new TreeSet<>();
        if (id.contains("/")) {
            return sameAs;
        }
        for (String otherId : otherIds) {
            if (otherId.equals(id)) {
                continue;
            }
            NavigableMap<String, ZoneRules> versions = ZoneRulesProvider.getVersions(otherId);
            ZoneRules otherRules = versions.get(version);
            if (otherRules != null && otherRules.equals(rules)) {
                sameAs.add(otherId);
            }
        }
        return sameAs;
    }

}
