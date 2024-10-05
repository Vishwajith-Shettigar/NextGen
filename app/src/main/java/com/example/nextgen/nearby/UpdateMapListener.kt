package com.example.nextgen.nearby

import com.example.model.Profile

interface UpdateMapListener {
  fun updateMap(profile: Profile,outOfBound:Boolean)
}
