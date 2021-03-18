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

package cl.bice.coe.bamboospecs.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.atlassian.bamboo.specs.api.validators.common.BambooStringUtils.containsRelaxedXssRelatedCharacters;

public class IllegalCharacterValidator implements ConstraintValidator<NoIllegalCharacters, String> {
   public boolean isValid(String obj, ConstraintValidatorContext context) {
      return !containsRelaxedXssRelatedCharacters(obj);
   }
}
