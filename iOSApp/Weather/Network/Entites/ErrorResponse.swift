//
//  ErrorResponse.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation

struct ErrorResponse: Codable {

  var cod     : String? = nil
  var message : String? = nil

  enum CodingKeys: String, CodingKey {

    case cod     = "cod"
    case message = "message"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    cod     = try values.decodeIfPresent(String.self , forKey: .cod     )
    message = try values.decodeIfPresent(String.self , forKey: .message )
 
  }

  init() {

  }

}
