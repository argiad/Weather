//
//  ContentView.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import SwiftUI
import UIKit

let networkHelper = NetworkHelper()

struct ContentView : View {
    @State private var searchText = ""
    
    @State private var _cities: [GeoCoding] = []
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(_cities, id: \.self) { city in
                    NavigationLink {
                        DetailView(city: city)
                        
                    } label: {
                        Text("\(city.name!) , \(city.state! )")
                    }
                }
            }
            .navigationTitle("Contacts")
        }
        .searchable(text: $searchText) {
            ForEach(_cities, id: \.self) { city in
                Text("\(city.name!)").searchCompletion(city.name!)
            }
        }
        .onChange(of: searchText) { newQuery in
            if (newQuery.isEmpty) {
                _cities.removeAll()
            } else {
                runSearch(matching: newQuery)
            }
        }
        .onAppear(){
            if !searchText.isEmpty {
                runSearch(matching: searchText )
            }
        }
        
    }
    
    func runSearch(matching: String) {
        networkHelper.getGeoData(for: matching) { result in
            switch result {
            case .success(let cities):
                _cities = cities
            case .failure(let e):
                _cities.removeAll()
            }
        }
    }
}

struct DetailView: View {
    
    var city: GeoCoding
    
    @State private var _weather: WeatherResponse? = nil
    
    var body: some View {
        NavigationStack {
            List {
                if $_weather.wrappedValue != nil {
                    let main = _weather!.main!
                    Section{
                        DetailLine(leftCeil: "Temperature", rightCeil: ": \(main.temp!)")
                        DetailLine(leftCeil: "Feels like", rightCeil: ": \(main.feelsLike!)")
                        DetailLine(leftCeil: "Humidity", rightCeil: ": \(main.humidity!)")
                        DetailLine(leftCeil: "Preasure", rightCeil: ": \(main.pressure!)")
                        
                    } header: {
                        Text ("main")
                    }.listRowSeparator(.hidden)
                    
                    let w = _weather!.weather!.first!
                    
                    Section{
                        DetailLine(leftCeil: "State", rightCeil: ": \(w.main!)")
                        DetailLine(leftCeil: "Description", rightCeil: ": \(w.description!)")
                    }header: {
                        HStack {
                            AsyncImage(url: URL(string: String(format:URLProvider.imageURL, w.icon! ))){ image in
                                image
                                    .resizable()
                                    .scaledToFill()
                            } placeholder: {
                                ProgressView()
                            }
                                .frame(width: 24, height: 24)
                            Text ("Weather")
                        }
                    }.listRowSeparator(.hidden)
                }
                
            }
            .navigationTitle("\(city.name!), \(city.state!)")
        }
        .onAppear{
            requestWeather()
        }
    }
    
    private func requestWeather(){
        networkHelper.getWeather(for: city) { result in
            switch result {
            case .success(let weather):
                _weather = weather
            case .failure(let e):
                print(e.localizedDescription)
            }
            
        }
    }
    
}

struct DetailLine: View {
    let leftCeil: String
    let rightCeil: String
    
    var body: some View {
        HStack{
            Text (leftCeil)
                .frame(maxWidth: .infinity, alignment: .leading)
            Text (rightCeil)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

#Preview {
    ContentView()
}


struct User: Identifiable {
    let id: UUID
    let name: String
}

struct UserModel{
    private(set) var users :[User] = []
    
    mutating func addUser(_ user: User){
        users.append(user)
    }
}

class UserViewModel: ObservableObject{
    @Published private var userModel =  UserModel()
    
    var users: [User] {
        return userModel.users
    }
    
    func addUser(_ user: User){
        userModel.addUser(user)
    }
}

