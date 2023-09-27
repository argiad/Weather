//
//  GeoCodingResponse.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation

struct GeoCoding: Codable, Hashable {

  var name    : String? = nil
  var lat     : Double? = nil
  var lon     : Double? = nil
  var country : String? = nil
  var state   : String? = nil

  enum CodingKeys: String, CodingKey {

    case name    = "name"
    case lat     = "lat"
    case lon     = "lon"
    case country = "country"
    case state   = "state"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    name    = try values.decodeIfPresent(String.self , forKey: .name    )
    lat     = try values.decodeIfPresent(Double.self , forKey: .lat     )
    lon     = try values.decodeIfPresent(Double.self , forKey: .lon     )
    country = try values.decodeIfPresent(String.self , forKey: .country )
    state   = try values.decodeIfPresent(String.self , forKey: .state   )
 
  }

  init() {

  }

}
