/*
 * Copyright (C) Copyright (C) 2010 Project Blindroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Software pattern Observer
 * Simple interface allowing for a particular part of the system to be observer.
 * We don't really need this software pattern anymore as we don't really have any background 
 * services that use it. It was created as a way to allow the StrokeCharacters to give it's updated
 * information to the various views that implemented it, however a listener interface would work just
 * as well, as demonstrated in the ShakeDetector. 
 */
package com.blindroid.utilities;

public interface Observer {
	public void update(String character);
}
