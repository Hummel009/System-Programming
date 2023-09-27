CXX       := g++
CXX_FLAGS := -std=c++17 -ggdb -shared -fPIC  # Added -shared and -fPIC for DLL

LD_FLAGS  := -luser32 -lgdi32

BIN        := bin
SRC        := src
BUILD      := build
INCLUDE    := -Iinclude

LIBRARIES   :=
DLL_NAME    := mylibrary.dll  # Name your DLL file
SRCS := $(wildcard $(SRC)/*.cpp)
OBJS := $(SRCS:$(SRC)/%.cpp=$(BUILD)/%.o)

all: $(BIN)/$(DLL_NAME)

$(BIN)/$(DLL_NAME): $(OBJS)
	$(CXX) $(CXX_FLAGS) $(INCLUDE) $^ -o $@ $(LIBRARIES) $(LD_FLAGS)

$(BUILD)/%.o: $(SRC)/%.cpp
	$(CXX) $(CXX_FLAGS) $(INCLUDE) -c $< -o $@ $(LIBRARIES) $(LD_FLAGS)

clean:
	-rm -rf $(BUILD)/*
	-rm $(BIN)/$(DLL_NAME)