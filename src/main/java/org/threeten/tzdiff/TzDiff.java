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
import java.util.NavigableMap;
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
        String outputVersion = "2014f-proposed";
        TreeSet<String> ids = new TreeSet<>(ZoneRulesProvider.getAvailableZoneIds());
        for (String id : ids) {
            String name = id.replace('/', '-') + ".txt";
            File file = new File("/dev/threeten/tzdiff/data", name);
            NavigableMap<String, ZoneRules> versions = ZoneRulesProvider.getVersions(id);
            ZoneRules rules = versions.get(outputVersion);
            if (rules != null) {
                try (FileWriter out = new FileWriter(file)) {
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
                }
            }
        }
    }

}
